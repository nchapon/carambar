(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]))


(fact (add-entry {:name "/path/test.jar" :values []}) => (contains {:name "/path/test.jar" :values []}))

(fact (create-entry "/path/test.jar") => {:name "/path/test.jar" :values []})

(def entry {:name "/path/test.jar" :values []})

(fact (update-entry entry "a/b/c/Test.class") => {:name "/path/test.jar" :values ["a/b/c/Test.class"]})

(def jarfile-with-two-classes "/home/nchapon/opt/m2_repo/org/carambar/simple/1.0-SNAPSHOT/simple-1.0-SNAPSHOT.jar")

(def slf4j "/home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.8/slf4j-api-1.7.8.jar")

(fact (:values (parse jarfile-with-two-classes)) => ["org.carambar.App" "org.carambar.MyClass"])


(defn filename->javaclass
  "Converts filename to fully qualified java class name"
  [filename]
  (when-let [matches (re-matches #"(.*).class$" filename)]
    (clojure.string/replace (second matches) "/" ".")))


(fact (filename->javaclass "a/b/c/MyClass.class") => "a.b.c.MyClass")
