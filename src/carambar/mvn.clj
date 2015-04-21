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

(defn mvn-output
  "doc-string"
  [output]
  (if (not-empty output)
    (format "-Doutput=%s" output)))

(defn mvn-pom
  "doc-string"
  [pom]
  (if (not-empty pom)
    ["-f" pom]))


(defn mvn
  "Runs mvn command"
  [{goal :goal pom :pom output :output}]
  (let [mvn-ret (apply sh/sh "mvn" goal (mvn-output output) (mvn-pom pom))]
    (if (= 0 (:exit mvn-ret))
       output
      (throw (Exception. (:out mvn-ret))))))

(defn mvn-settings
  "doc-string"
  [settings]
  (let [xz (zip/xml-zip (xml/parse settings))]
    (zx/xml1-> xz :localRepository zx/text)))

(def effective-settings-path (format "%s/mvn-settings.xml" (System/getProperty "java.io.tmpdir")))

(def effective-pom-path
  (format "%s/effective-pom.xml" (System/getProperty "java.io.tmpdir")))


(defn mvn-help:effective-settings
  "Maven help:effective settings command."
  []
  (mvn {:goal "help:effective-settings"
        :output effective-settings-path}))

(def local-repo
  (-> (mvn-help:effective-settings)
      (mvn-settings)))

(defn mvn-help:effective-pom
  [project-dir]
  (mvn {:goal "help:effective-pom"
        :pom (format "%s/pom.xml" project-dir)
        :output effective-pom-path}))

(defn expand-dependency-path
  "Expand dependency path with maven repo path. "
  [{group :groupId artifact :artifactId version :version}]
  (format "%s/%s/%s/%s/%s-%s.jar"
          local-repo
          (clojure.string/replace group "." "/")
          artifact version
          artifact version))

(defn project-info
  "Creates project information from POM file"
  [pom]
  (let [xz (zip/xml-zip (xml/parse pom))]
    (merge
     {:project (zx/xml1-> xz :artifactId zx/text)}
     {:classpath (map expand-dependency-path (dependencies xz))})))

(defn read-project-info
  "Process maven project from DIR"
  [project-dir]
  (->
     (mvn-help:effective-pom project-dir)
     (project-info)))
