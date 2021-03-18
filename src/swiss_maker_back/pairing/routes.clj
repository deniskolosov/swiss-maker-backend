(ns swiss-maker-back.pairing.routes
  (:require [swiss-maker-back.responses :as responses]
            [swiss-maker-back.pairing.handlers :as pairing]))


(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/pairings" {:swagger {:tags ["pairing"]}}
     ["/:tournament-id" ["/:round-no"
                         {:get  {:handler    (pairing/get-pairing db)
                                 :parameters {:path {:tournament-id int?
                                                     :round-no      int?}}
                                 :responses  {200 {:body responses/pairing}}
                                 :summary    "List pairings for round"}
                          :post {:handler    (pairing/create-pairing! db)
                                 :parameters {:path {:tournament-id int?
                                                     :round-no      int?}}
                                 :responses  {201 {:body responses/pairing}}
                                 :summary    "Create pairings for round"}}]]]))
