(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]))


(fact (add-entry {:name "/path/test.jar" :values []}) => (contains {:name "/path/test.jar" :values []}))

;; need to run mvn clean install before...
(def jarfile-with-two-classes "/home/nchapon/opt/m2_repo/org/carambar/simple/1.0-SNAPSHOT/simple-1.0-SNAPSHOT.jar")

(def slf4j "/home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.8/slf4j-api-1.7.8.jar")

(fact (:values (parse jarfile-with-two-classes)) => (contains ["org.carambar.App" "org.carambar.MyClass"] :in-any-order))

(fact (filename->javaclass "a/b/c/MyClass.class") => "a.b.c.MyClass")
