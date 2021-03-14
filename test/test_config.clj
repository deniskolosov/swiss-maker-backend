(ns test-config
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [clj-http.client :as http-client]
            [ring.mock.request :as mock]
            [swiss-maker-back.core :as core]
            [next.jdbc.sql :as sql])
  (:import java.net.ServerSocket))

(defonce test-system (atom nil))

(defn random-port []
  (with-open [s (ServerSocket. 0)]
    (.getLocalPort s)))

(defn test-config []
  (let [test-port (random-port)]
    (-> (core/system-config :test)
        ;; some bug? with integrant where port is busy,
        ;; solved with a repl restart or this
        (assoc-in [:core/jetty :port] test-port)
        (assoc-in [:db/postgres :jdbc-url] "jdbc:postgresql://localhost/swiss_maker_test")
        )))


(defn test-env [] (:swiss-maker-back/env @test-system))

(defn halt []
  (swap! test-system #(if % (ig/halt! %))))

(defn go []
  (ig-repl/halt)
  (reset! test-system (ig/init (test-config))))

(defn test-system-fixture-runner [init-test-data testfunc]
  (try
    (go)
    (init-test-data)
    (testfunc)
    (finally
      (halt))))

(defn call-api [verb path headers body]
  (let [my-port (-> @test-system :core/jetty .getConnectors first .getPort)
        my-fn   (cond
                  (= verb :get)    http-client/get
                  (= verb :post)   http-client/post
                  (= verb :put)    http-client/put
                  (= verb :delete) http-client/delete)]
    (select-keys
      (my-fn (str "http://localhost:" my-port "/" path)
             {:as               :json
              :form-params      body
              :headers          headers
              :content-type     :json
              :throw-exceptions true
              :coerce           :always}
             ) [:status :body])))


(comment
  (go)
  (call-api :get "v1/tournaments" nil nil)
  (call-api :post "v1/tournaments" nil {:name          "sdf"
                                        :num-of-rounds 10})
  (http-client/post "http://localhost:3000/v1/tournaments"
                    {:as               :json
                     :form-params      {:name          "hello"
                                        :num-of-rounds 4}
                     :headers          nil
                     :content-type     :json
                     :throw-exceptions true
                     :coerce           :always} [:status :body])
  (halt)
  (test-config)
  (keys (deref test-system))
  (ig/halt! @test-system)
  (def app (-> @test-system :swiss-maker-back/app))
  (def db (-> @test-system :db/postgres ))
  (jdbc/execute! db [" drop table  if exists tournament cascade; "])
  (jdbc/execute! db ["create table tournament (
  id serial not null primary key,
  name text,
  num_of_rounds int not null,
  current_round int check (current_round >= 0) default 0,
  unique(id)
)"])
  (jdbc/execute! db ["select * from tournament"])

  (call-api :put "/v1/players/11f0fe1d-893e-4559-b280-a324d173bce6" nil {:id            "11f0fe1d-893e-4559-b280-a324d173bce6"
                                                                         :name          "Ivan Ivanov"
                                                                         :rating        1200
                                                                         :current-score 5})

  (defn test-endpoint
    ([method uri]
     (test-endpoint method uri nil))
    ([method uri opts]
     (let [app     (-> @test-system :swiss-maker-back/app)
           request (app (-> (mock/request method uri)
                            (cond-> (:body opts) (mock/json-body (:body opts)))))]
       (update request :body (partial m/decode "application/json")))))

  (test-endpoint :get "/v1/tournaments")
  (sql/insert! db :tournament {:name          "My tournament"
                               :num-of-rounds 5}))

