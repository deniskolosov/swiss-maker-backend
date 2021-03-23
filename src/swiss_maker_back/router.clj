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
            [swiss-maker-back.player.routes :as player]
            [swiss-maker-back.pairing.routes :as pairing]
            [ring.middleware.cors :refer [wrap-cors]]
            [expound.alpha :as expound]
            [reitit.ring.middleware.dev :as dev]))

(defn coercion-error-handler [status]
  (let [printer (expound/custom-printer {:theme :figwheel-theme, :print-specs? false})
        handler (exception/create-coercion-handler status)]
    (fn [exception request]
      (printer (-> exception ex-data :problems))
      (handler exception request))))

(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc  true
     :swagger {:basePath "/"
               :info     {:title       "Swiss Maker API"
                          :description "Api for Swiss Maker"
                          :version     "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate  rs/validate
   :exception pretty/exception
   :data      {:coercion   coercion-spec/coercion
               :muuntaja   m/instance
               :middleware [swagger/swagger-feature
                            muuntaja/format-middleware
                            ;; exception/exception-middleware
                            #(wrap-cors %
                                        :access-control-allow-origin [#".*"]
                                        :access-control-allow-methods [:get :post])
                            ;; (exception/create-exception-middleware
                            ;;   (merge
                            ;;     exception/default-handlers
                            ;;     {:reitit.coercion/request-coercion  (coercion-error-handler 400)
                            ;;      :reitit.coercion/response-coercion (coercion-error-handler 500)}))
                            coercion/coerce-request-middleware
                            coercion/coerce-response-middleware]}})

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [swagger-docs
       ["/v1"
        (tournament/routes env)
        (player/routes env)
        (pairing/routes env)]]
      router-config)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))
