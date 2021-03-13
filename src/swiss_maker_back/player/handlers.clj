(ns swiss-maker-back.player.handlers
  (:require [ring.util.response :as rr]
            [swiss-maker-back.player.db :as player-db]))


(def get-players
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          players (player-db/get-players-by-tournament-id db tournament-id)]
      (if players
        (rr/response players)
        (rr/not-found {:type "tournament-not-found"
                       :message "Tournament not found"
                       :data (str "tournament-id" tournament-id)})))))

;; TODO: write update player
(def update-player!
  [db]
  (fn [request]
    (let [tournament-id (-> request :parameters :path :tournament-id)
          player-id (-> request :parameters :path :player-id)
          player (-> request :parameters :body)
          update-successful? (player-db/update-player! db (assoc player :id player-id))
          ]
      )))
