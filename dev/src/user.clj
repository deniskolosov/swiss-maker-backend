(ns user
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [ring.util.response :as rr]
            [swiss-maker-back.pairing.db :as pairing-db]
            [swiss-maker-back.pairing.handlers :refer [get-pairing]]
            [swiss-maker-back.player.db :as player-db]
            [swiss-maker-back.tournament.db :refer [insert-tournament!]]
            [swiss-maker-back.tournament.handlers :refer [create-tournament!]]))

(ig-repl/set-prep!
  (fn [] (aero/read-config (io/resource "config.edn") {:profile :dev})))


(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)


(def app (-> state/system :swiss-maker-back/app))
(def db (-> state/system :db/postgres))


(comment
  (go)
  (halt)
  (reset)
  (reset-all)
  (jdbc/execute! db ["truncate"])
  (insert-tournament! db {:name "LEts gfooo" :num-of-rounds 10})

  (jdbc/execute! db ["insert into tournament(name, num_of_rounds) values (?, ?)" "hello" 10])
  (if ( seq (rr/response (pairing-db/get-pairing-for-round db 1 1))) "yes" "no")
  ((get-pairing db) {:request-method :get
                     :parameters     {:path {:round-no      1
                                             :tournament-id 1}}})

  (jdbc/execute! db ["insert into pairing(white_id,black_id , board_no , tournament_id , round_no) values
(?,?,?,?,?)" "c6d60a7a-8997-419f-8a34-5edf719f0b5b" "1ef53c2c-a326-4d43-8813-67f0c75ac055", 1, 1 , 1])
  (set! *print-namespace-maps* false)
  (m/decode "application/json" (:body (app {:request-method :post
                                            :uri            "/v1/tournaments"
                                            :body           {"num-of-rounds" 5
                                                             "ame"           "hello"}})))
  (map-indexed #())


  
  (defn create-pair
    ;; Create a pairing map
    [b w]
    (-> {}
        (assoc :white-id (:player/id w))
        (assoc :black-id (:player/id b))
        (assoc :result -1)))

  (defn pair-players [players]
    (let [w (last players)
          b (first players)]
      (when (> ( count players) 1)
        (list* (create-pair b w)
               (pair-players (rest (drop-last players)))))))



  (def players' '({:id            "c6d60a7a-8997-419f-8a34-5edf719f0b5b"
                   :name          "Ivan Ivanov"
                   :rating        1000
                   :current-score 0
                   :tournament-id 1},
                  {:id            "1ef53c2c-a326-4d43-8813-67f0c75ac055"
                   :name          "Petr Petrov"
                   :rating        1200
                   :current-score 0
                   :tournament-id 1}
                  {:id            "a0758ad6-1e7e-459c-9a80-5b8344c74978"
                   :name          "Fedor Sokolov"
                   :rating        1450
                   :current-score 0
                   :tournament-id 1}
                  {:id            "c01c099c-1ec1-4350-a274-6f16772b1b54"
                   :name          "Nikolay Kozlov"
                   :rating        1300
                   :current-score 0
                   :tournament-id 1}
                  {:id            "df2330a4-0f4d-4603-a3a6-502b6a990dd1"
                   :name          "Semen Fedorov"
                   :rating        1500
                   :current-score 0
                   :tournament-id 1}
                  ))

  (map-indexed #(-> {}
                    (assoc :white-id (:id %2))
                    (assoc :black-id (:id %2))
                    (assoc :board-no (+ % 1))) players')
  ;; function which returns first and last items recursively
  (defn pair [lst]
    (if (> ( count lst) 1)
      (list* (last lst)(first lst)
             (pair (rest (drop-last lst))))
      lst))

  ;; using pair as example, return list of pairing maps
  ;; {:white-id string? :black-id string? :board-no int? :result (-2 or 0.5)
  ;;  :}

  (defn mid-element
    ;;  return mid-element of an odd-numbered list
    [l]
    (nth l (quot (count l) 2)))

  (pairing-db/create-pairing! db 1 1)


  (let [players
        (sort-by :rating (:players (player-db/get-players-by-tournament-id db 1)))]
    (mid-element players))


  ;; last player  will get a bye and won't appear in pairings
  (odd? ( count ( pair-players players')))
  (pairing-db/same-color-twice? :black "c6d60a7a-8997-419f-8a34-5edf719f0b5b" db 3 1)



  (pairing-db/create-round db 1 1)
  (pairing-db/create-pairing! db 1 1)

  ;; => (#:player{:id "c6d60a7a-8997-419f-8a34-5edf719f0b5b", :name "Ivan
  ;; Ivanov", :rating 1000, :current-score 0, :tournament-id 1} #:player{:id
  ;; "1ef53c2c-a326-4d43-8813-67f0c75ac055", :name "Petr Petrov", :rating 1200,
  ;; :current-score 0, :tournament-id 1} #:player{:id
  ;; "c01c099c-1ec1-4350-a274-6f16772b1b54", :name "Nikolay Kozlov", :rating
  ;; 1300, :current-score 0, :tournament-id 1} #:player{:id
  ;; "a0758ad6-1e7e-459c-9a80-5b8344c74978", :name "Fedor Sokolov", :rating 1450,
  ;; :current-score 0, :tournament-id 1} #:player{:id
  ;; "df2330a4-0f4d-4603-a3a6-502b6a990dd1", :name "Semen Fedorov", :rating 1500,
  ;; :current-score 0, :tournament-id 1})


  (def mylist '({:name "Denis" :age 10 } {:name "Ivan" :age 50} {:name "Boris" :age 21} {:name "Boris" :age 30}))
  (rest (butlast (reverse mylist)))

  ;; return a map which looks like {:name "Denis" :friends []}
  (loop [people mylist
         me     (first mylist)]
    (let [candidate (first elements)]
      (if (= (:age candidate) 10)
        (print "hello")
        (recur person (rest elements)))))


  (sql/find-by-keys db :player {:tournament_id 1})
  (sql/find-by-keys db :tournament {:id 1})
  (-> (sql/update! db :player {:current-score 8} (select-keys {:id "867ed4bf-4628-48f4-944d-e6b7786bfa92"} [:id]))
      :next.jdbc/update-count
      (pos?))
  (-> (sql/delete! db :player {:id "867ed4bf-4628-48f4-944d-e6b7786bfa92"})
      :next.jdbc/update-count
      (pos?))

  (sql/insert! db :tournament {:num-of-rounds 5 :name "hello"})
  ((create-tournament! db) {:parameters {:body {:num-of-rounds 5 :name "hello"}}})
  (:tournament/id  (insert-tournament! db {:num-of-rounds 6 :name "FFOO"}) )



  (with-open [conn (jdbc/get-connection db)]
    (let [tournaments (jdbc/execute! conn ["select * from tournament"])]
      {:tournaments tournaments}))


  )


