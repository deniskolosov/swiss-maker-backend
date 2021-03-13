(ns swiss-maker-back.core
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as njc]
            [ring.adapter.jetty :as jetty]
            [swiss-maker-back.router :as router])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn app
  [env]
  (router/routes env))

(defmethod aero/reader 'ig/ref [_ _ value] (ig/ref value))

(defmethod ig/prep-key :core/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :swiss-maker-back/profile [_ profile]
  profile)

(defmethod ig/init-key :swiss-maker-back/env [_ env]
  env)

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
  (jdbc/with-options
    (njc/->pool HikariDataSource {:jdbcUrl jdbc-url})
    jdbc/snake-kebab-opts))

(defmethod ig/halt-key! :db/postgres
  [_ config]
  (.close ^HikariDataSource (:connectable config)))

(defn read-config [profile]
  (aero/read-config (io/resource "config.edn") {:profile profile}))

(defn system-config [myprofile]
  (let [profile (or myprofile (some-> (System/getenv "PROFILE") keyword) :dev)
        _ (log/info "Using profile " profile)
        config (read-config profile)]
    config))

(defn -main
  []
  (let [config (system-config nil)]
    (-> config ig/prep ig/init)))
