(ns swiss-maker-back.router
  (:require [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.spec :as rs]
            [swiss-maker-back.tournament.routes :as tournament]
            [reitit.ring.middleware.dev :as dev]))




(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc true
     :swagger {:basePath "/"
               :info {:title "Swiss Maker API"
                      :description "Api for Swiss Maker"
                      :version "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate rs/validate
   :exception pretty/exception
   :data {:coercion coercion-spec/coercion
          :muuntaja m/instance
          :middleware [swagger/swagger-feature
                       muuntaja/format-middleware
                       ;; exception/exception-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})

(defn routes
  [env]
  (ring/ring-handler
   (ring/router
    [swagger-docs
     ["/v1"
      (tournament/routes env)]]
    router-config)
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"}))))
