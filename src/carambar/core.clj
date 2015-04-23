(ns carambar.core
  (:use ring.util.response)
  (:require [carambar.repository :as repository]
            [carambar.mvn :as mvn]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.json :as middleware]
            [clojure.tools.logging :as log]))

(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (POST "/projects" req
        (let [path (get-in req [:body :path])]
          (response (repository/add-project path))))
  (GET "/projects/:name/classes" [name search] (response {:classes (repository/find-class name search)})))

(defn init
  "Init carambar settings"
  []
  (log/info "Init carambar : " mvn/local-repo))


(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body {:keywords? true})
             (middleware/wrap-json-response)))
