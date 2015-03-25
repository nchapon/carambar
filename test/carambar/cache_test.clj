(ns carambar.cache-test
  (:require [midje.sweet :refer :all]
            [carambar.cache :refer :all]
            [clojure.java.io :as io])
  (:import [java.util.zip ZipEntry ZipOutputStream]))


(fact "Add entry"
  (add-entry {:name "/path/test.jar" :values []}) => (contains {:name "/path/test.jar" :values []}))

(fact "Convert filename to javaclas."
  (filename->javaclass "a/b/c/MyClass.class") => "a.b.c.MyClass")

(def tmpdir (System/getProperty "java.io.tmpdir"))

(defmacro ^:private with-entry
  [zip entry-name & body]
  `(let [^ZipOutputStream zip# ~zip]
     (.putNextEntry zip# (ZipEntry. ~entry-name))
     ~@body
     (flush)
     (.closeEntry zip#)))

(defn jarfile [filename classes]
  (with-open [file (io/output-stream (str tmpdir "/" filename))
              zip  (ZipOutputStream. file)
              wrt  (io/writer zip)]
    (binding [*out* wrt]
      (dotimes [i (count classes)]
        (with-entry zip (nth classes i)
          (println "foo"))))))

(with-redefs [boot-classpath ["/tmp/foo.jar" "/tmp/bar.jar"]]
  (with-state-changes
    [(before :facts (do
                      (jarfile "foo.jar" ["Foo.class" "foo/Bar.class"])
                      (jarfile "bar.jar" ["bar/Foo.class" "bar/Baz.class"])
                      (reset! cache [])))]
    (fact "Should have two classes when Jar has two files"
      (parse "/tmp/foo.jar") => {:name "/tmp/foo.jar" :values ["Foo" "foo.Bar"]})
    (fact "Cache should have two entries"
      (count (create-cache)) => 2)))

(with-state-changes
  [(before :facts (do
                    (reset! cache [])
                    (add-entry {:name "foo.jar" :values ["com.foo.Bar" "com.foo.Baz"]})))]
  (fact "Find class by name"
    (find-class "Baz") => [{:name "foo.jar" :values ["com.foo.Baz"]}]))
