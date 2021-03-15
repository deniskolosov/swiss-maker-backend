(ns swiss-maker-back.player.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn get-players-by-tournament-id
  [db tournament-id]
  (let [players (sql/find-by-keys db :player {:tournament_id tournament-id})]
    {:players players}))

(defn update-player!
  [db player]
  (-> (sql/update! db :player player (select-keys player [:id]))
      :next.jdbc/update-count
      (pos?))
  )

(defn insert-player!
  [db {:keys [id name rating current-score, tournament-id]}]
  (jdbc/execute-one! db ["insert into player(id, name, rating, current_score,
                          tournament_id) values (?, ?, ?, ?, ?)"
                         id name rating current-score, tournament-id] {:return-keys true}))

(defn delete-player!
  [db player]
  (-> (sql/delete! db :player player)
      :next.jdbc/update-count
      (pos?))
  )
