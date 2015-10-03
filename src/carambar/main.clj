(ns carambar.main
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]
            [carambar.core :refer :all]))


(defn- start-server [handler port]
  (let [server (run-server handler {:port port})]
    (println (str "Start carambar on port " port))
    server))

(defn- stop-server [server]
  (when (server)
    (server)))

(defrecord Carambar []
    component/Lifecycle
    (start [this]
      (assoc this :server (start-server #'app 3000)))

    (stop [this]
      (stop-server (:server this))
      (dissoc this :server)))

(defn create-system []
  (Carambar.))

(defn -main [& args]
  (start-server #'app 3000))
