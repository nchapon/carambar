(ns carambar.jtag)

(def clazz (atom [{:class "Class"
                   :package "org.clojure.test"}
                  {:class "AnotherClass"
                   :package "org.clojure.another"}]))

(defn add-class
  "Add class c with package p"
  [c p]
  (swap! clazz conj {:class c :package p}))

(defn wrap-results
  "Wrap results"
  [results]
  (when-not (nil? results)
    {:data (vector {:package results})}))

(defn query-pakage-for-class
  "Query package for classname"
  [c]
  (wrap-results (:package (first (filter #(= (% :class) c) @clazz)))))
