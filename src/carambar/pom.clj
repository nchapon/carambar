(ns carambar.pom
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.data.zip.xml :as zx]))


(defn attr-map
  "doc-string"
  [loc ks]
  (reduce (fn [m k]
     (assoc m k (zx/xml1-> loc k zx/text)))
   {}
   ks))

(defn dependency [dependency]
  (attr-map dependency [:groupId :artifactId :version]))

(defn dependencies
  [pom-zip]
  (zx/xml-> pom-zip :dependencies :dependency dependency))

(defn process-pom
  "Process maven POM xml file"
  [pom]
  (let [xz (zip/xml-zip (xml/parse pom))]
    (merge
     {:project (zx/xml1-> xz :artifactId zx/text)}
     {:dependencies (dependencies xz)})))
