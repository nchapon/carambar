(ns carambar.jtag-test
  (:require [midje.sweet :refer :all]
            [carambar.jtag :refer :all]))


;; /home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.6/slf4j-api-1.7.6-sources.jar

(def filename "/home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.6/slf4j-api-1.7.6-sources.jar")



(defn filenames-in-zip
  "doc-string"
  [f]
  (let [z (java.util.zip.ZipFile. f)]
    (map #(.getName %) (enumeration-seq (.entries z)))))


(filenames-in-zip filename)



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

(fact (query-pakage-for-class "Class") => {:data [{:package "org.clojure.test"}]})

(fact (query-pakage-for-class "Clazz") => nil)

(fact (add-class "C2" "P2") => (contains {:class "C2" :package "P2"}))
