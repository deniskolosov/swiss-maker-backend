(ns user
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [swiss-maker-back.tournament.db :refer [insert-tournament!]]
            [swiss-maker-back.player.handlers :refer [add-player!]]
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
  ((add-player! db) {:parameters {:path {:tournament-id 1}
                                  :body {:name          "Denis"
                                         :rating        1000
                                         :current-score 5}}})


  (set! *print-namespace-maps* false)
  (m/decode "application/json" (:body (app {:request-method :post
                                            :uri            "/v1/tournaments"
                                            :body           {"num-of-rounds" 5
                                                             "ame"           "hello"}})))
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
