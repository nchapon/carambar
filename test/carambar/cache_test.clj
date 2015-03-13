(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]
            [clojure.java.io :as io])
  (:import [java.util.zip ZipEntry ZipOutputStream]))


(fact (add-entry {:name "/path/test.jar" :values []}) => (contains {:name "/path/test.jar" :values []}))

;; need to run mvn clean install before...
(def jarfile-with-two-classes "/home/nchapon/opt/m2_repo/org/carambar/simple/1.0-SNAPSHOT/simple-1.0-SNAPSHOT.jar")

(def slf4j "/home/nchapon/opt/m2_repo/org/slf4j/slf4j-api/1.7.8/slf4j-api-1.7.8.jar")

(fact (:values (parse jarfile-with-two-classes)) => (contains ["org.carambar.App" "org.carambar.MyClass"] :in-any-order))

(fact (filename->javaclass "a/b/c/MyClass.class") => "a.b.c.MyClass")


(def tmpdir (System/getProperty "java.io.tmpdir"))




;;(.mkdir (java.io.File. (str tmpdir "/carambar")))

;;(map #(.mkdir (java.io.File. (str  tmpdir "/carambar/" %))) ["a" "a/b" "a/b/c"])

;;(map  #(spit (str tmpdir "/carambar/a/b/c/" %) "CONTENT") ["A.class"])


(defmacro ^:private with-entry
  [zip entry-name & body]
  `(let [^ZipOutputStream zip# ~zip]
     (.putNextEntry zip# (ZipEntry. ~entry-name))
     ~@body
     (flush)
     (.closeEntry zip#)))

(defn jarfile [filename]
  (with-open [file (io/output-stream (str tmpdir "/" filename))
              zip  (ZipOutputStream. file)
              wrt  (io/writer zip)]
    (binding [*out* wrt]
      (doto zip
        (with-entry "Foo.class"
          (println "foo"))
        (with-entry "bar/Baz.class"
          (println "baz"))))))


;; (with-open [output (ZipOutputStream. (io/output-stream "foo.zip"))
;;             input  (io/input-stream "foo")]
;;   (with-entry output "foo"
;;     (io/copy input output)))


(jarfile "test.jar")


(parse (str tmpdir "/" "test.jar"))
