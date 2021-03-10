(ns user
  (:require [integrant.core :as ig]
            [integrant.repl.state :as state]
            [integrant.repl :as ig-repl]
            [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn" slurp ig/read-string)))


(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)


(def app (-> state/system :swiss-maker-back/app))
(def db (-> state/system :db/postgres))


(comment
  (sql/query db ["select * from tournament"])

  (go)
  (halt)
  (reset)
  (reset-all))
