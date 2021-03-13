(ns swiss-maker-back.player.routes
  (:require [swiss-maker-back.player.handlers :as player]
            [swiss-maker-back.responses :as responses]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/players" {:swagger {:tags ["players"]}}
     ["/:tournament-id"
      ["" {:get {:handler (player/get-players db)
                 :parameters {:path {:tournament-id int?}}
                 :responses {200 {:body responses/players}}
                 :summary "Get tournament"}}]
      ["/:player-id"
       ["" {:put {:handler (player/update-player! db)
                  :parameters {:path {:player-id int?}
                               :body {:name string?
                                      :rating int?
                                      :score int?}}
                  :responses {204 {:body nil?}}
                  :summary "Update player"}
            :delete {:handler (player/delete-player! db)
                     :parameters {:path {:player-id int?}}
                     :responses {204 {:body nil?}}
                     :summary "Delete player"}}]]]]))
