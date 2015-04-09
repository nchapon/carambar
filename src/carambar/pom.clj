(ns carambar.pom
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.shell :as sh]
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

(defn project-info
  "Creates project information from POM file"
  [pom]
  (let [xz (zip/xml-zip (xml/parse pom))]
    (merge
     {:project (zx/xml1-> xz :artifactId zx/text)}
     {:dependencies (dependencies xz)})))

(defn process-project
  "Process maven project from DIR"
  [project-dir]
  (let [mvn-ret (sh/sh "mvn" "-f" (str project-dir "/pom.xml") "help:effective-pom" "-Doutput=effective-pom.xml")]
    (if (= 0 (:exit mvn-ret))
      (project-info (str project-dir "/effective-pom.xml"))
      (:out mvn-ret))))
