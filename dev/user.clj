(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [carambar.core]))

(reloaded.repl/set-init! #'carambar.core/create-system)
