(ns carambar.core
  (:require [carambar.repository :as repository]
            [carambar.mvn :as mvn]
            [com.stuartsierra.component :as component]
            [ring.util.response :refer [response status]]
            [compojure.core :refer [GET POST defroutes]]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
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

;; Chain middlewares with handler
(def app
  (-> (wrap-defaults app-routes api-defaults) ;; can get query params
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)))
