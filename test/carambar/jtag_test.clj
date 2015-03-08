(ns carambar.jtag-test
  (:require [midje.sweet :refer :all]
            [carambar.jtag :refer :all]))


;; /home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.6/slf4j-api-1.7.6-sources.jar

(def filename "/home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.2/slf4j-api-1.7.2-sources.jar")


(defn java-file?
  "Is java file"
  [f]
  (re-matches #".*\.java$" f))

(defn entries
  "Zipfile entries"
  [zipfile]
  (enumeration-seq (.entries zipfile)))


(defn read-content
  "read content"
  [entry zipfile]
  (when (java-file? (.getName entry))
    (with-open [rdr (clojure.java.io/reader
                     (java.io.InputStreamReader. (.getInputStream zipfile entry)))]
      (reduce conj [] (line-seq rdr)))))


(defn filter-package
  "doc-string"
  [lines]
  (filter #(re-matches #"^package (.*);$" %) lines))

(defn filenames-in-zip
  "doc-string"
  [f]
  (let [z (java.util.zip.ZipFile. f)]
    (map #(filter-package (read-content % z)) (entries z))))


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
