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
  </dependencies>
</project>")


(def xml-tree
  (-> data
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse ))

(def pom (zip/xml-zip xml-tree))

(fact (zip-xml/xml1-> pom :name zip-xml/text) => "simple")
