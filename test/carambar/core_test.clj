(ns carambar.core-test
  (:require [carambar.core :refer :all]
            [midje.sweet :refer :all]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.data.zip.xml :as zip-xml]))


(def data "<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.carambar</groupId>
  <artifactId>simple</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  <name>simple</name>
  <url>http://maven.apache.org</url>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.5</version>
    </dependency>
  </dependencies>
</project>")


(def xml-tree
  (-> data
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse ))

(def pom (zip/xml-zip xml-tree))

(fact (zip-xml/xml1-> pom :name zip-xml/text) => "simple")

(fact (zip-xml/xml-> pom :dependencies :dependency (zip-xml/tag= :groupId) zip-xml/text) => ["junit" "org.slf4j"])

(defn dependency->map [dependency]
  {:groupId (zip-xml/xml1-> dependency :groupId zip-xml/text)
   :artifactId (zip-xml/xml1-> dependency :artifactId zip-xml/text)
   :version (zip-xml/xml1-> dependency :version zip-xml/text)
   :scope (zip-xml/xml1-> dependency :scope zip-xml/text)})

(defn pom->map
  []
  (let [pom (zip/xml-zip xml-tree)]
    {:dependencies (mapv dependency->map (zip-xml/xml-> pom :dependencies :dependency))}))

(pom->map)
