(ns carambar.system-test
  (:require [midje.sweet :refer :all]
            [carambar.system :refer :all]))

(when (= (get-os-name) "win")
  (fact (mvn-command) => "mvn.bat"))

(when-not (= (get-os-name) "win")
  (fact (mvn-command) => "mvn"))
