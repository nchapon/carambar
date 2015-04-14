(ns carambar.mvn
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

(defn mvn
  "Runs mvn command"
  [goal & options]
  (let [mvn-ret (apply sh/sh "mvn" goal options)]
    (if-not (= 0 (:exit mvn-ret))
      (throw (Exception. (:out mvn-ret))))))

(defn mvn-settings
  "doc-string"
  [settings]
  (let [xz (zip/xml-zip (xml/parse settings))]
    (zx/xml1-> xz :localRepository zx/text)))

(defn mvn-help:effective-settings
  "Maven help:effective settings command."
  []
  (mvn "help:effective-settings" "-Doutput=mvn-settings.xml"))

(def local-repo
  (do (mvn-help:effective-settings)
      (mvn-settings "mvn-settings.xml")))

(defn mvn-help:effective-pom
  [project-dir]
  (mvn "help:effective-pom"
       "-f" (format "%s/pom.xml" project-dir)
       "-Doutput=effective-pom.xml"))

(defn read-pom
  "Process maven project from DIR"
  [project-dir]
  (do
    (mvn-help:effective-pom project-dir)
    (project-info (format "%s/effective-pom.xml" project-dir))))
