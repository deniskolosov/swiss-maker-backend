(ns swiss-maker-back.pairing.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [swiss-maker-back.player.db :as player-db]))

(defn played-before?
  [p1 p2 db]
  (pos? (:count (jdbc/execute-one! db
                                   ["select COUNT(*)
                                    from pairing where (white_id = ?)
                                    and (black_id = ?)" p1 p2]))))
(defn same-color-twice?
  [color pid db round-no tournament-id]
  (let [param (if (= color :white) "white_id" "black_id")]
    (if (> round-no 2)
      (= 2 (:count (jdbc/execute-one! db [(str "select count(*) from pairing
                          where " param " = ? and round_no in (?, ?) and tournament_id = ?;")
                                          pid
                                          (- round-no 2)
                                          (- round-no 1)
                                          tournament-id])))
      false)))



(defn create-pair
  ;; Create a pairing map
  [b w]
  (-> {}
      (assoc :white-id (:player/id w))
      (assoc :black-id (:player/id b))
      (assoc :result -1)))


;; last player  will get a bye and won't appear in pairings
(defn pair-players [players]
  (let [w (first players)
        b (last players)]
    (when (> ( count players) 1)
      (list* (create-pair b w)
             (pair-players (rest (drop-last players)))))))

(defn last-round?
  [db tournament-id]
  (let [tournament (sql/get-by-id db :tournament tournament-id)]
    (= (:tournament/num-of-rounds tournament) (:tournament/current-round tournament))))

(defn mid-element
  ;;  return mid-element of an odd-numbered list
  [l]
  (nth l (quot (count l) 2)))

(defn insert-pairings!
  [db pairings-list tournament-id round-no]
  (map-indexed (fn [index pair]
                 (sql/insert! db :pairing (-> pair
                                              (assoc :tournament-id tournament-id)
                                              (assoc :round-no round-no)
                                              (assoc :board-no (+ index 1)))))
               pairings-list))

(defn create-round
  "
  * Get players in given tournament.
  * Sort them by rating or score depending on round
  * Create pairing map by taking first player of top half and placing them
    on board 1 with top player of bottom half playing black pieces
  * If player number is odd, unpaired player gets 0.5 as result
  "
  [db tournament-id round-no]
  (let [sort-param    (if (> round-no 1) :player/current-score :player/rating)
        players       (sort-by sort-param (:players (player-db/get-players-by-tournament-id db tournament-id)))
        pairings-list (pair-players players)]
    (when (odd? (count players))
      ;; update score of an unpaired player
      (let [unpaired-player (mid-element players)
            new-score       (+ 0.5 (:player/current-score unpaired-player))
            id              (:player/id unpaired-player)]
        (sql/update! db :player {:current-score new-score } {:id id})))
    (insert-pairings! db pairings-list tournament-id round-no)))

(defn create-pairing!
  "Given tournament id and round number, create pairing for the next round.

  Pairings are created using following rules:
  * In the first round, players are ranked by their rating, then top half is paired
    with bottom half.
  * In the next rounds following restrictions apply:
    * Players who played before cannot be paired again
    * Player cannot have 3 whites or blacks in a row
    * Subsequently, players who have two whites or two blacks in a row, cannot play
      other players who have two whites or two blacks respectively, otherwise they would end
      with 3 whites or blacks. This restriction can be lifted in the last round if otherwise
      pairing is not possible.
  "
  [db tournament-id round-no]
  (create-round db tournament-id round-no))

(defn get-pairing-for-round
  [db tournament-id round-no]
  (jdbc/execute! db ["select * from pairing where (tournament_id = ?) and (round_no = ?)
" tournament-id round-no]))

