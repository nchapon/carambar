(defproject carambar "0.1.0-SNAPSHOT"
  :description "Carambar !!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main carambar.main
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [http-kit "2.1.18"]
                 [com.cemerick/pomegranate "0.3.0"]
                 [compojure "1.3.4"]
                 [com.stuartsierra/component "0.3.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [org.eclipse.jdt.core.compiler/ecj "4.4.2"]
                 [environ "1.0.1"]]
  :profiles {:dev {:plugins []
                   :dependencies [[reloaded.repl "0.2.0"]
                                  [midje "1.6.3"]]
                   :source-paths ["dev"]
                   }}
  :uberjar-name "carambar.jar")
