(ns swiss-maker-back.tournament.routes
  (:require [swiss-maker-back.tournament.handlers :as tournament]
            [swiss-maker-back.responses :as responses]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/tournaments" {:swagger {:tags ["tournaments"]}}
     [""
      {:get  {:handler   (tournament/list-all-tournaments db)
              :responses {200 {:body responses/tournaments}}
              :summary   "List all tournaments"}
       :post {:handler    (tournament/create-tournament! db)
              :parameters {:body {:name          string?
                                  :num-of-rounds int?}}
              :responses  {201 {:body {:tournament/id int?}}}
              :summary    "Create tournament"}}]
     ["/:tournament-id"
      ["" {:get    {:handler    (tournament/get-tournament db)
                    :parameters {:path {:tournament-id int?}}
                    :responses  {200 {:body responses/tournament}}
                    :summary    "Get tournament"}
           :put    {:handler    (tournament/update-tournament! db)
                    :parameters {:path {:tournament-id int?}
                                 :body {:name          string?
                                        :num-of-rounds int?}}
                    :responses  {204 {:body nil?}}
                    :summary    "Update tournament"}
           :delete {:handler    (tournament/delete-tournament! db)
                    :parameters {:path {:tournament-id int?}}
                    :responses  {204 {:body nil?}}
                    :summary    "Delete tournament"}}]]]))
