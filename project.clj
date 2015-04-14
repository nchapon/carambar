(defproject carambar "0.1.0-SNAPSHOT"
  :description "Carambar !!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [ring/ring-core "1.3.2"]
                 [compojure "1.3.2"]
                 [ring/ring-json "0.3.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [javax.servlet/servlet-api "2.5"]]}}
  :plugins [[lein-ring "0.9.2"]]
  :uberjar-name "carambar.jar"
  :ring {:handler carambar.core/app
         :init carambar.core/init})
