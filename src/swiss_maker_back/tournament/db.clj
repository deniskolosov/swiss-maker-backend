(ns swiss-maker-back.tournament.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defn get-all-tournaments
  [db]
  (with-open [conn (jdbc/get-connection db)]
    (let [tournaments (sql/query conn ["select * from tournament"])]
      {:tournaments tournaments})))


(defn insert-tournament!
  [db {:keys [name num-of-rounds]}]
  (jdbc/execute-one! db ["insert into tournament(name, num_of_rounds) values (?, ?)" name num-of-rounds] {:return-keys true}))



(defn get-tournament-by-id
  [db tournament-id]
  (sql/find-by-keys db :tournament {:id tournament-id}))

(defn update-tournament!
  [db tournament]
  (-> (sql/update! db :tournament tournament (select-keys tournament [:id]))
      :next.jdbc/update-count
      (pos?)))

(defn delete-tournament!
  [db tournament]
  (-> (sql/delete! db :tournament tournament)
      :next.jdbc/update-count
      (pos?)))
