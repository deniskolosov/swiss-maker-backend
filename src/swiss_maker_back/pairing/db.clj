(ns swiss-maker-back.pairing.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [swiss-maker-back.player.db :as player-db]))


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

;; TODO: add board-no to created pairings
(defn first-round
  "
  * Get players in given tournament.
  * Sort them by rating
  * Create pairing map by taking first player of top half and placing them
    on board 1 with top player of bottom half playing black pieces
  * If player number is odd, unpaired player gets 0.5 as result
  "
  [db tournament-id round-no]
  (let [players       (sort-by :player/rating (:players (player-db/get-players-by-tournament-id db tournament-id)))
        pairings-list (pair-players players)]
    (when (odd? (count players))
      ;; update score of an unpaired player
      (let [unpaired-player (mid-element players)
            new-score       (+ 0.5 (:player/current-score unpaired-player))
            id              (:player/id unpaired-player)]
        (sql/update! db :player {:current-score new-score } {:id id})))
    (for [pairing pairings-list]
      (sql/insert! db :pairing (-> pairing
                                   (assoc :tournament-id tournament-id)
                                   (assoc :round-no round-no))))
    ))

(defn create-pairing
"Given tournament id and round number, create pairing for the next round.

  Pairings are created using following rules:
  * In the first round, players are ranked by their rating, then top half is paired
    with bottom half.
  * In the next rounds following restrictions apply:
    * Players who played before cannot be paired again
    * Player cannot have 3 whites in a row
    * Subsequently, players who have two whites or two blacks in a row, cannot play
      other players who have two whites or two blacks respectively, otherwise they would end
      with 3 whites or blacks. This restriction can be lifted in the last round if otherwise
      pairing is not possible.
  "
  [db tournament-id round-no]
  ;; (case
  ;;     (= round-no 1) #()
  ;;     (last-round? db tournament-id) #()
  ;;     #()
  ;;     )
  )

(defn get-pairing-for-round
  [db tournament-id round-no]
  (jdbc/execute! db ["select * from pairing where (tournament_id = ?) and (round_no = ?)
" tournament-id round-no]))

(defn create-pairing-for-round!
  [db tournament-id round-no]
(let [pairing (create-pairing db tournament-id round-no)]
    (sql/insert! db :pairing pairing)))

