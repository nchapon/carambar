(ns carambar.pom
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.shell :as sh]
            [clojure.data.zip.xml :as zx]))





(defn mvn-settings
  "doc-string"
  [settings]
  (let [xz (zip/xml-zip (xml/parse settings))]
    (zx/xml1-> xz :localRepository zx/text)))

(def mvn-local-repo
  (let [mvn-ret (sh/sh "mvn" "help:effective-settings" "-Doutput=mvn-settings.xml")]
                      (if (= 0 (:exit mvn-ret))
                        (mvn-settings "mvn-settings.xml")
                        (:out mvn-ret))))

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





(defn mvn-effective-pom
  [project-dir]
  (let [mvn-ret (sh/sh "mvn" "help:effective-pom" "-f" (str project-dir "/pom.xml")  "-Doutput=effective-pom.xml")]
    (if-not (= 0 (:exit mvn-ret))
      (throw (Exception. (:out mvn-ret))))))


(defn process-project
  "Process maven project from DIR"
  [project-dir]
  (do
    (mvn-effective-pom project-dir)
    (project-info (str project-dir "/effective-pom.xml"))))
