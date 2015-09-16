(ns carambar.core
  (:require [carambar.repository :as repository]
            [carambar.mvn :as mvn]
            [com.stuartsierra.component :as component]
            [ring.util.response :refer [response status]]
            [compojure.core :refer [GET POST defroutes]]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer :all]))

(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (POST "/projects" {:keys [body]}
        (let [path (:path body)]
          (repository/add-project path)
          (status (response "") 201)))
  (POST "/test" {:keys [body]}
        (response (slurp body)))
  (GET "/projects/:name/classes" [name search]
       (response {:classes (repository/find-class name search)}))
  (GET "/projects" []
       (response {:projects (repository/list-projects)})))

(defn init
  "Init carambar settings"
  []
  (log/info "Init carambar : " mvn/local-repo))

;; Chain middlewares with handler
(def app
  (-> (wrap-defaults app-routes api-defaults) ;; can get query params
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)))

(defn- start-server [handler port]
  (let [server (run-server handler {:port port})]
    (init)
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
