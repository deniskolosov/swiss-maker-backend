(ns user
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [next.jdbc :as jdbc]
            [ring.util.response :as rr]))

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

  (def my-handler
    [db]
    (fn [request]
      (let [tournament-id (-> request :parameters :path :tournament-id)]
        (rr/not-found {:message (str "hello" tournament-id)}))))


  (set! *print-namespace-maps* false)
  (app {:request-method :get
        :uri "/v1/tournaments/1"})

  (with-open [conn (jdbc/get-connection db)]
    (let [tournaments (jdbc/execute! conn ["select * from tournament"])]
      {:tournaments tournaments}))


  )
