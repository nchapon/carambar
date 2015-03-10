(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]))


(fact (add-entry {:name "/path/test.jar" :values []}) => (contains {:name "/path/test.jar" :values []}))

(fact (create-entry "/path/test.jar") => {:name "/path/test.jar" :values []})

(def entry {:name "/path/test.jar" :values []})

(fact (update-entry entry "/path/a/b/Test.class") => {:name "/path/test.jar" :values ["/path/a/b/Test.class"]})
