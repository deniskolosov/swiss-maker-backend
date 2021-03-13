(ns swiss-maker-back.core-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [test-config :refer [call-api test-system test-system-fixture-runner]]))

(defn init-fixture []
  (let [db (-> @test-system :db/postgres )]
    (jdbc/execute! db [" drop table  if exists tournament cascade; "])
    (jdbc/execute! db ["create table tournament (
                        id serial not null primary key,
                        name text,
                        num_of_rounds int not null,
                        current_round int check (current_round >= 0) default 0,
                        unique(id))"])
    (sql/insert-multi! db :tournament [:name :num-of-rounds] [["First test tournament" 5]
                                                              ["Second test tournament" 8]])))

(defn user-test-fixture
  [t]
  (println "ENTER user-test-fixture")
  (test-system-fixture-runner init-fixture t)
  (println "EXIT user-test-fixture"))

(use-fixtures :each user-test-fixture)


(deftest tournament-tests
  (testing "List tournaments"
    (let [{:keys [status body]} (call-api :get "/v1/tournaments" nil nil)]
      (is (= 200 status))
      (is (= [{:tournament/name "First test tournament",
               :tournament/id 1,
               :tournament/current_round 0,
               :tournament/num_of_rounds 5}
              {:tournament/name "Second test tournament",
               :tournament/id 2,
               :tournament/current_round 0,
               :tournament/num_of_rounds 8}] (:tournaments body))))))


(comment
  (call-api :get "/v1/tournaments" nil nil))


