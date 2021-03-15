(ns swiss-maker-back.core-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [test-config :refer [call-api test-system test-system-fixture-runner]])
  (:import java.util.UUID))

(defn init-fixture []
  (let [db (-> @test-system :db/postgres )]
    (jdbc/execute! db ["truncate table tournament, player restart identity cascade;"])
    ;; (sql/insert-multi! db :tournament [:id :name :num-of-rounds] [[1 "First test tournament" 5]
    ;;                                                               [2 "Second test tournament" 8]])
    ;; (sql/insert-multi! db :player [:id :name :rating :current-score :tournament-id]
    ;;                    [["11f0fe1d-893e-4559-b280-a324d173bce6" "Ivan Ivanov" 1200 3 1]
    ;;                     ["aba04e95-6cad-4a1f-88cf-66f47f4557dc" "Semen Petrov" 1300 4 1]
    ;;                     ["217cba35-bd06-4d8d-ae7b-d8b19e0a5dfe" "Petr Semenov" 1500 1 1]
    ;;                     ["ebe758dc-2dad-4d91-bd72-61e64adc6693" "Ivan Ivanov" 1600 3 2]
    ;;                     ["1bc49fbc-97ca-4ad0-9927-503651b5714e" "Semen Petrov" 1700 0 2]
    ;;                     ["03f71105-ecc8-44a3-be2c-3574cb09507e" "Petr Semenov" 1350 5 2]])
    ))

(defn user-test-fixture
  [t]
  (println "ENTER user-test-fixture")
  (test-system-fixture-runner init-fixture t)
  (println "EXIT user-test-fixture"))

(use-fixtures :each user-test-fixture)


(deftest tournament-tests
  (testing "Create tournament"
    (let [{:keys [status body]} (call-api :post "v1/tournaments" nil {:name          "Test tournament"
                                                                      :num-of-rounds 10})]
      (is (= 201 status))
      (is (= {:tournament/id            1,
              :tournament/name          "Test tournament",
              :tournament/num-of-rounds 10,
              :tournament/current-round 0} body))))
  (testing "List tournaments"
    (let [{:keys [status body]} (call-api :get "v1/tournaments" nil nil)]
      (is (= 200 status))
      (is (= [{:tournament/id            1,
               :tournament/name          "Test tournament",
               :tournament/num_of_rounds 10,
               :tournament/current_round 0}] (:tournaments body))))))


(deftest players-tests
  ;; (testing "Add player"
  ;;   (let [{:keys [status body]} (call-api :post "/v1/players/1/" nil {:name          "Petr Petrov"
  ;;                                                                     :rating        1400
  ;;                                                                     :current-score 4})]
  ;;     (is (= 201 status))
  ;;     (is (= {} body))))

  (testing "Get players"
    (let [{:keys [status body]} (call-api :get "v1/players/1" nil nil)]
      (is (= 200 status))
      ;; (is (= [{:player/id            "11f0fe1d-893e-4559-b280-a324d173bce6",
      ;;          :player/name          "Ivan Ivanov",
      ;;          :player/rating        1200,
      ;;          :player/current-score 3,
      ;;          :player/tournament-id 1}
      ;;         {:player/id            "aba04e95-6cad-4a1f-88cf-66f47f4557dc",
      ;;          :player/name          "Semen Petrov",
      ;;          :player/rating        1300,
      ;;          :player/current-score 4,
      ;;          :player/tournament-id 1}
      ;;         {:player/id            "217cba35-bd06-4d8d-ae7b-d8b19e0a5dfe",
      ;;          :player/name          "Petr Semenov",
      ;;          :player/rating        1500,
      ;;          :player/current-score 1,
      ;;          :player/tournament-id 1}] (:players body)))
      ))
  (testing "Update player"
    (let [request-body          {:id            "11f0fe1d-893e-4559-b280-a324d173bce6"
                                 :name          "Ivan Ivanov"
                                 :rating        1200
                                 :current-score 5}
          {:keys [status body]} (call-api :put "v1/players/1/11f0fe1d-893e-4559-b280-a324d173bce6" nil request-body)]
      (is (= 204 status))
      (is (= nil body))))
  (testing "Delete player"
    (let [{:keys [status body]} (call-api :delete "v1/players/1/11f0fe1d-893e-4559-b280-a324d173bce6" nil nil)]
      (is (= 204 status))
      (is (= nil body)))))

(comment
  (call-api :get "/v1/tournaments" nil nil))


