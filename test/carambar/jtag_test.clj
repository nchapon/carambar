(ns carambar.jtag-test
  (:require [midje.sweet :refer :all]
            [carambar.jtag :refer :all]))


(def lines (clojure.string/split-lines (slurp "test/App.java")))


(defn package
  "doc-string"
  [s]
  (when-let [matches (re-matches #"^package (.*);$" s)]
    (second matches)))


(fact (->> lines
           (map package)
           (remove nil?)
           (first)) => "org.carambar.jx")

(fact (query-pakage-for-class "Class") => "org.clojure.test")

(fact (query-pakage-for-class "Clazz") => nil)
