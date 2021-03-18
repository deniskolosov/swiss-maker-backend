(ns swiss-maker-back.pairing.handlers
  (:require [swiss-maker-back.pairing.db :as pairing-db]
            [ring.util.response :as rr]
            [swiss-maker-back.responses :as responses]))


(defn get-pairing
  [db]
  (fn [req]
    (let [tournament-id (-> req :parameters :path :tournament-id)
          round-no      (-> req :parameters :path :round-no)
          pairing       (pairing-db/get-pairing-for-round db tournament-id round-no)]
      (if pairing
        (rr/response {:pairing pairing})
        (rr/not-found {:type    "pairing-not-found"
                       :message "Pairing not found. Perhaps create it?"
                       :data    (str "tournament-id, " tournament-id "round-no, " round-no)})))))

(defn create-pairing!
  [db]
  (fn [req]
    (let [tournament-id (-> req :parameters :path :tournament-id)
          round-no      (-> req :parameters :path :round-no)
          pairing       (pairing-db/create-pairing! db tournament-id round-no)]
      (rr/created (str responses/base-url "/pairings" (:id pairing)) {:pairing pairing}))))
