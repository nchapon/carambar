(ns carambar.core
  (:use ring.util.response)
  (:require [carambar.repository :as repository]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (GET "/index" [] (response (repository/create-cache)))
  (GET "/classes" [search] (response {:classes ["java.util.List" "java.util.ArrayList"]})))

(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body)
             (middleware/wrap-json-response)))
