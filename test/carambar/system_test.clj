(ns carambar.system-test
  (:require [midje.sweet :refer :all]
            [carambar.system :refer :all]))







(fact (mvn-command) => "mvn.bat")
