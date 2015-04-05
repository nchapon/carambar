(ns carambar.pom-test
  (:require [midje.sweet :refer :all]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [carambar.pom :refer :all]))


(def content "<project>
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


(def pom-xml
  (-> content
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse
    zip/xml-zip))

(fact "Load dependencies"
  (dependencies pom-xml) =>  [{:artifactId "junit", :groupId "junit", :version "4.11"} {:artifactId "slf4j-api", :groupId "org.slf4j", :version "1.7.5"}])

(fact "Load from pom.xml"
  (process-pom "test/resources/projects/simple/pom.xml") =not=> nil?)
