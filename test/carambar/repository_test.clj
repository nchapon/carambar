(ns carambar.repository-test
  (:require [midje.sweet :refer :all]
            [carambar.repository :refer :all]
            [clojure.java.io :as io])
  (:import [java.util.zip ZipEntry ZipOutputStream]))


(fact "Add entry"
  (add-entry {:artifactid "/path/test.jar" :classes []}) => (contains {:artifactid "/path/test.jar" :classes []}))

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
                      (reset! repo [])))]
    (fact "Should have two classes when Jar has two files"
      (parse "/tmp/foo.jar") => {:artifactid "/tmp/foo.jar" :classes ["Foo" "foo.Bar"]})
    (fact "Cache should have two entries"
      (count (create-cache)) => 2)))

(with-state-changes
  [(before :facts (do
                    (reset! repo [])
                    (add-entry {:artifactid "foo.jar" :classes ["com.foo.Bar" "com.foo.Baz"]})
                    (add-entry {:artifactid "bar.jar" :classes ["com.bar.Bar" "com.bar.Buzz"]}))
           )]
  (fact "Find class by name"
    (find-class "Baz") => ["com.foo.Baz"]
    (find-class "Ba") => ["com.foo.Bar" "com.foo.Baz" "com.bar.Bar"]
    ))

(facts "Filter cache entry by classname"
  (filter-entry {:artifactid "foo.jar" :classes ["com.foo.Bar" "com.foo.Baz"]} "Baz") => ["com.foo.Baz"]
  (filter-entry {:artifactid "foo.jar" :classes ["com.foo.Bar" "com.foo.Baz"]} "ZZ") => [])


(facts "Match class name starts with."
  (match-class? "Baz" "com.foo.Baz") => false
  (match-class? "Baz" "com.foo.BazBar") => true
  (match-class? "Baz" "com.foo.FooBaz") => false)

(facts "Match class name exactly."
  (match-class-exactly? "Baz" "com.foo.Baz") => true
  (match-class-exactly? "Baz" "com.foo.BazBar") => false
  (match-class-exactly? "Baz" "com.foo.FooBaz") => false)
