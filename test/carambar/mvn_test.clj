(ns carambar.mvn-test
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.string :as str]
            [carambar.mvn :refer :all]
            [midje.sweet :refer :all])
  (:use [clojure.test]))


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


(defn pom-zip [xml]
  (-> xml
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse
    zip/xml-zip))


(def pom-xml (pom-zip content))

(fact "Should read dependencies from pom.xml in a vector"
  (let [deps '[[junit "4.11"] [org.slf4j/slf4j-api "1.7.5"]]]
    (dependencies pom-xml) => deps))


(facts "Transform dependency map in a vector"
  (dependency-vec {:groupId "org.slf4j", :artifactId "slf4j-api", :version "1.7.5"}) => '[org.slf4j/slf4j-api "1.7.5"]
  (dependency-vec {:groupId "junit", :artifactId "junit", :version "4.11"}) => '[junit "4.11"])

(comment "Should be rewrite !!"
  (with-redefs [local-repo "/m2_repo"]
    (fact "Should expand dependency path."
      (expand-dependency-path
       {:artifactId "mockito-core",
        :groupId "org.mockito",
        :version "1.10.8"}) => "/m2_repo/org/mockito/mockito-core/1.10.8/mockito-core-1.10.8.jar")
    (fact "Read pom file."
      (read-project-info "test_projects/simple")
      => {:project "simple"
          :classpath
          ["/m2_repo/junit/junit/4.11/junit-4.11.jar"
           "/m2_repo/org/mockito/mockito-core/1.10.8/mockito-core-1.10.8.jar"
           "/m2_repo/org/easytesting/fest-assert/1.4/fest-assert-1.4.jar"
           "/m2_repo/org/slf4j/slf4j-api/1.7.8/slf4j-api-1.7.8.jar"
           "/m2_repo/ch/qos/logback/logback-classic/1.1.2/logback-classic-1.1.2.jar"
           "/m2_repo/ch/qos/logback/logback-core/1.1.2/logback-core-1.1.2.jar"]})))
