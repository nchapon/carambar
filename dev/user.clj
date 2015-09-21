(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [carambar.main :refer [create-system]]))

(reloaded.repl/set-init! #(create-system))
