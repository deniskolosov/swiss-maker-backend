(ns swiss-maker-back.tournament.handlers
  (:require [ring.util.response :as rr]
            [swiss-maker-back.responses :as responses]
            [swiss-maker-back.tournament.db :as tournament-db])
  (:import java.util.UUID))

(defn list-all-tournaments
[db]
(fn [req]
  (let [tournaments (tournament-db/get-all-tournaments db)]
    (rr/response tournaments))))

(defn create-tournament!
  [db]
  (fn [request]
    (let [tournament-id (str (UUID/randomUUID))
          tournament (-> request :parameters :body)]
      (tournament-db/insert-tournament! db (assoc tournament :tournament-id tournament-id))
      (rr/created (str responses/base-url "/tournaments" tournament-id)))))


(defn get-tournament
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          tournament (tournament-db/get-tournament-by-id db tournament-id)]
      (if tournament
        (rr/response tournament)
        (rr/not-found {:type "tournament-not-found"
                       :message "Tournament not found"
                       :data (str "tournament-id" tournament-id)})))))

(defn update-tournament!
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          tournament (-> request :parameters :body)
          update-successful? (tournament-db/update-tournament! db (assoc tournament :id tournament-id))]
          (if update-successful?
            (rr/status 204)
            (rr/not-found {:type "tournament-not-found"
                           :message "Tournament not found"
                           :data (str "tournament-id" tournament-id)})))))


(defn delete-tournament!
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          deleted? (tournament-db/delete-tournament! db {:id tournament-id})]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:type "tournament-not-found"
                       :message "Tournament not found"
                       :data (str "tournament-id" tournament-id)})))))
