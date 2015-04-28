(ns carambar.repository-test
  (:require [midje.sweet :refer :all]
            [carambar.repository :refer :all]
            [carambar.mvn :as mvn]
            [clojure.java.io :as io])
  (:import [java.util.zip ZipEntry ZipOutputStream]))


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

(with-state-changes
  [(before :facts (do
                    (jarfile "foo.jar" ["Foo.class" "foo/Bar.class"])
                    (jarfile "bar.jar" ["bar/Foo.class" "bar/Baz.class"])))]
  (fact "Foo.jar should have two classes when Jar has two files"
    (get-jar-content "/tmp/foo.jar") => {:artifactid "/tmp/foo.jar" :classes ["Foo" "foo.Bar"]}
  (fact "Classpath should have two entries"
    (count (get-classes-from-classpath ["/tmp/foo.jar" "/tmp/bar.jar"])) => 2)))


(with-state-changes
  [(before :facts (do
                    (reset! projects [])
                    (add-project! {:project "simple"
                                   :classes [{:artifactid "foo.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}
                                             {:artifactid "bar.jar" :classes ["com.bar.Kix" "com.bar.Baz"]}]})
                    ))]
  (facts "Filter repository by predicate"
    (find-class "simple" "Baz") => ["com.foo.Baz" "com.bar.Baz"]
    (find-class "simple" "Ba") => ["com.foo.Bar" "com.foo.Baz" "com.bar.Baz"]))

(facts "Match class name starts with."
  (name-starts-with? "Baz" "com.foo.Baz") => nil
  (name-starts-with? "Baz" "com.foo.BazBar") => "com.foo.BazBar"
  (name-starts-with? "Baz" "com.foo.FooBaz") => nil)

(facts "Match class name exactly."
  (has-name? "Baz" "com.foo.Baz") => "com.foo.Baz"
  (has-name? "Baz" "com.foo.BazBar") => nil
  (has-name? "Baz" "com.foo.FooBaz") => nil)


(fact "Make project"
  (make-project "/path/toproject/simple") => {:project "simple"
                                              :classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"]
                                              :classes [{:artifactid "aid-1.0.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}]}
  (provided
    (mvn/read-project-info "/path/toproject/simple") => {:project "simple"
                                                         :classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"]}
    (get-classes-from-classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"]) => [{:artifactid "aid-1.0.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}]))
