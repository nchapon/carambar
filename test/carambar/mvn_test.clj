(ns carambar.mvn-test
  (:require [midje.sweet :refer :all]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.string :as str]
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

(fact "Mvn local repo should be initialized."
  (str/blank? local-repo) => false)

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
       "/m2_repo/ch/qos/logback/logback-core/1.1.2/logback-core-1.1.2.jar"]}))
