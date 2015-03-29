(ns carambar.core
  (:use ring.util.response)
  (:require [carambar.cache :as cache]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (GET "/index" [] (response (cache/create-cache)))
  (GET "/classes" [name] (response {:classes ["java.util.List" "java.util.ArrayList"]})))

(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body)
             (middleware/wrap-json-response)))
