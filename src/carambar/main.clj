(ns carambar.main
  (:require [com.stuartsierra.component :as component]))


;; (defn- start-server [handler port]
;;   (let [server (run-server handler {:port port})]
;;     (init)
;;     (println (str "Start carambar on port " port))
;;     server))

;; (defn- stop-server [server]
;;   (when (server)
;;     (server)))

(defrecord Carambar []
    component/Lifecycle
    (start [this]
      (println  "Start Carambar"))

    (stop [this]
      (println  "Stop Carambar")))

(defn create-system []
  (Carambar.))

;; (defn -main [& args]
;;   (start-server #'app 3000))
