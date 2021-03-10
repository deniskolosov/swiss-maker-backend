(ns swiss-maker-back.core
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [swiss-maker-back.router :as router]
            [next.jdbc :as jdbc]))


(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :core/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :core/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :swiss-maker-back/app
  [_ config]
  (println "\nStarted app")
  (app config))


(defmethod ig/halt-key! :core/jetty
  [_ jetty]
  (.stop jetty))

(defmethod ig/init-key :db/postgres
  [_ {:keys [jdbc-url]}]
  (println "\nConfigured db")
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))
