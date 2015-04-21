(ns carambar.core
  (:use ring.util.response)
  (:require [carambar.repository :as repository]
            [carambar.mvn :as mvn]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :as middleware]
            [clojure.tools.logging :as log]))

(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (GET "/projects" [] (response (repository/add-project "test_projects/simple")))
  (GET "/projects/classes" [search] (response {:classes (repository/find-class search)})))

(defn init
  "Init carambar settings"
  []
  (log/info "Init carambar : " mvn/local-repo))


(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body)
             (middleware/wrap-json-response)))
