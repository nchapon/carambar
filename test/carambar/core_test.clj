(ns carambar.core-test
  (:require [clojure.test :refer :all]
            [carambar.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))


(comment
  (require '[clojure.xml :as xml])
  (xml/parse "test/pom.xml"))
