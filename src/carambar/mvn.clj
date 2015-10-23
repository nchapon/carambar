(ns carambar.mvn
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.shell :as sh]
            [clojure.data.zip.xml :as zx]
            [environ.core :refer [env]]
            [carambar.system :as sys]
            [cemerick.pomegranate.aether :as aether]))




(defn- group-artifact
  [groupId artifactId]
  (if (= groupId artifactId)
    (symbol artifactId)
    (symbol groupId artifactId)))

(defn dependency-vec
  "Transform a dependency map into dependency vector"
  [m]
  (let [{:keys [artifactId groupId version]} m]
    [(group-artifact groupId artifactId) version]))

(defn attr-map
  "doc-string"
  [loc ks]
  (reduce (fn [m k]
     (assoc m k (zx/xml1-> loc k zx/text)))
   {}
   ks))

(defn dependency-map [dependency]
  (attr-map dependency [:groupId :artifactId :version]))


(defn dependencies
  [pom-zip]
  (map dependency-vec
       (zx/xml-> pom-zip :dependencies :dependency dependency-map)))

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
  (let [mvn-ret (apply sh/sh (sys/mvn-command) goal (mvn-output output) (mvn-pom pom))]
     (if (= 0 (:exit mvn-ret))
       output
      (throw (Exception. (:out mvn-ret))))))


(def effective-pom-path
  (format "%s/effective-pom.xml" (System/getProperty "java.io.tmpdir")))

(def local-repo (env :mvn-repository))

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
