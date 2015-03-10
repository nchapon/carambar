(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]))


(fact (add-entry "/path/test.jar") => [{:entry "/path/test.jar"}])

(fact (update-entry "/path/test.jar") => [{:entry "/path/test.jar"}])
