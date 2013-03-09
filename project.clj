(defproject fz "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [compojure "1.1.3"]
                 [ring/ring-core "1.1.7"]
                 [hiccup "1.0.2"]
                 [environ "0.3.0"]]
  :plugins [[lein-ring "0.7.3"]
            [environ/environ.lein "0.3.0"]]
  :hooks [environ.leiningen.hooks]
  :env {:store-path "/tmp"}
  :ring {:handler fz.core/app})
