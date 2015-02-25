(ns carambar.core
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :as middleware]))



(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (GET "/packages" [q] (response [{:package "org.apache.logging"}
                                 {:package "com.google.guava"}])))

(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body)
             (middleware/wrap-json-response)))
