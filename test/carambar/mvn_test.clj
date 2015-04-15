(ns carambar.mvn-test
  (:require [midje.sweet :refer :all]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [carambar.mvn :refer :all]))


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

(fact "Read dependencies"
  (dependencies pom-xml) =>  [{:artifactId "junit", :groupId "junit", :version "4.11"} {:artifactId "slf4j-api", :groupId "org.slf4j", :version "1.7.5"}])

(fact "Read pom file."
  (read-pom "test_projects/simple")
  => {:project "simple"
      :dependencies
      [{:artifactId "junit", :groupId "junit", :version "4.11"}
       {:artifactId "mockito-core", :groupId "org.mockito", :version "1.10.8"}
       {:artifactId "fest-assert", :groupId "org.easytesting", :version "1.4"}
       {:artifactId "slf4j-api", :groupId "org.slf4j", :version "1.7.8"}
       {:artifactId "logback-classic", :groupId "ch.qos.logback", :version "1.1.2"}
       {:artifactId "logback-core", :groupId "ch.qos.logback", :version "1.1.2"}]})

(fact "Mvn local repo should be initialized."
  local-repo => "/home/nchapon/opt/m2_repo")

(with-redefs [local-repo "/m2_repo"]
  (fact "Should expand dependency path."
    (expand-dependency-path
     {:artifactId "mockito-core",
      :groupId "org.mockito",
      :version "1.10.8"}) => "/m2_repo/org/mockito/mockito-core/1.10.8/mockito-core-1.10.8.jar"))