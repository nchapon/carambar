(ns carambar.jtag-test
  (:require [midje.sweet :refer :all]
            [carambar.jtag :refer :all]))


;; /home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.6/slf4j-api-1.7.6-sources.jar

(def filename "/home/nchapon/opt/m2_repo/org/carambar/simple/1.0-SNAPSHOT/simple-1.0-SNAPSHOT-sources.jar")


(defn java-file?
  "Is java file"
  [f]
  (re-matches #".*\.java$" f))

(defn entries
  "Zipfile entries"
  [zipfile]
  (filter #(java-file? (.getName %)) (enumeration-seq (.entries zipfile))))

(defn read-content
  "read content"
  [entry zipfile]
  (with-open [rdr (clojure.java.io/reader
                   (java.io.InputStreamReader. (.getInputStream zipfile entry)))]
    (reduce conj [] (line-seq rdr))))


(defn package
  "doc-string"
  [s]
  (when-let [matches (re-matches #"^package (.*);$" s)]
    (second matches)))


(defn filter-classname
  "doc-string"
  [filename]
  (when-let [matches (re-matches #".*\/(.*).java$" filename)]
    (second matches)))


(defn filter-package
  "doc-string"
  [lines]
  (->> lines
           (map package)
           (remove nil?)
           (first)))

(defn create-class [entry zipfile]
  {:class (filter-classname (.getName entry))
   :package (filter-package (read-content entry zipfile))})


(defn filenames-in-zip
  "doc-string"
  [f]
  (let [z (java.util.zip.ZipFile. f)]
    (map #(create-class % z) (entries z))))


(fact (filenames-in-zip filename) => [{:class "App" :package "org.carambar"}])

(def jarfile "/home/nchapon/opt/m2_repo/org/carambar/simple/1.0-SNAPSHOT/simple-1.0-SNAPSHOT.jar")
(let [z (java.util.zip.ZipFile. jarfile)]
    (map #(.getName %) (enumeration-seq (.entries z))))

(def classname "org/carambar/App.java")

(when-let [matches (re-matches #".*\/(.*).java$" classname)]
    (second matches))



(def lines (clojure.string/split-lines (slurp "test/resources/projects/simple/src/main/java/org/carambar/App.java")))

(fact (->> lines
           (map package)
           (remove nil?)
           (first)) => "org.carambar")

(fact (query-pakage-for-class "Class") => {:data [{:package "org.clojure.test"}]})

(fact (query-pakage-for-class "Clazz") => nil)

(fact (add-class "C2" "P2") => (contains {:class "C2" :package "P2"}))
