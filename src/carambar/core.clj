(ns carambar.core
  (:use ring.util.response)
  (:require [carambar.jtag :as jtag]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :as middleware]))


(defroutes app-routes
  (GET "/" [] (response {:carambar "OK"}))
  (GET "/packages" [classname] (response (jtag/query-pakage-for-class classname))))

(def app (-> (handler/api app-routes)
             (middleware/wrap-json-body)
             (middleware/wrap-json-response)))
