(ns carambar.jtag)

(def clazz [{:class "Class"
             :package "org.clojure.test"}
            {:class "AnotherClass"
             :package "org.clojure.another"}])


(defn query-pakage-for-class
  "Query package for classname"
  [c]
  (:package (first (filter #(= (% :class) c) clazz))))
