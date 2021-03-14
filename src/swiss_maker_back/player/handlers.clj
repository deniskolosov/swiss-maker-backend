(ns swiss-maker-back.player.handlers
  (:require [ring.util.response :as rr]
            [swiss-maker-back.player.db :as player-db]))


(defn get-players
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          players       (player-db/get-players-by-tournament-id db tournament-id)]
      (if players
        (rr/response players)
        (rr/not-found {:type    "tournament-not-found"
                       :message "Tournament not found"
                       :data    (str "tournament-id" tournament-id)})))))

(defn update-player!
  [db]
  (fn [request]
    (let [tournament-id      (-> request :parameters :path :tournament-id)
          player-id          (-> request :parameters :path :player-id)
          player             (-> request :parameters :body)
          update-successful? (player-db/update-player! db (assoc player :id player-id))
          ]
      (if update-successful?
        (rr/status 204)
        (rr/not-found {:type    "player-update-error"
                       :message "Something went wrong"
                       :data    (str "tournament-id: " tournament-id "player-id: " player-id)})))))

(defn delete-player!
  [db]
  (fn [request]
    (let [player-id (-> request :parameters :path :player-id)
          deleted?  (player-db/delete-player! db {:id player-id})
          ]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:type    "player-not-found"
                       :message "Player not found"
                       :data    (str "player-id: " player-id)})))))
