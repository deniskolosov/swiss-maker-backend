(defproject swiss-maker-back "0.1.0-SNAPSHOT"
  :description "Backend for Swiss Maker - app to create swiss tournaments"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.1"]
                 [integrant "0.8.0"]
                 [environ "1.2.0"]
                 [metosin/reitit "0.5.5"]
                 [camel-snake-kebab "0.4.1"]
                 [aero "1.1.6"]
                 [seancorfield/next.jdbc "1.1.582"]
                 [com.zaxxer/HikariCP "3.4.5"]
                 [org.postgresql/postgresql "42.2.14"]
                 [metosin/muuntaja "0.6.8"]
                 [cheshire "5.10.0"]
                 [clj-http "3.12.1"]
                 [org.clojure/tools.trace "0.7.11"]
                 [expound "0.8.9"]]
  :plugins [[cider/cider-nrepl "0.25.5"]]
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :dependencies [[integrant/repl "0.3.1"]
                                  [ring/ring-mock "0.4.0"]]}}
  :uberjar-name "swiss-maker-back")
