(ns carambar.repository-test
  (:require [midje.sweet :refer :all]
            [carambar.repository :refer :all]
            [carambar.mvn :as mvn]
            [clojure.java.io :as io])
  (:import [java.util.zip ZipEntry ZipOutputStream]))


(fact "Convert filename to javaclas."
  (filename->javaclass "a/b/c/MyClass.class") => "a.b.c.MyClass")

(def tmpdir (System/getProperty "java.io.tmpdir"))

(defn expand-filename
  "Expand FILENAME with DIR"
  [dir filename]
  (str dir "/" filename))


(defmacro ^:private with-entry
  [zip entry-name & body]
  `(let [^ZipOutputStream zip# ~zip]
     (.putNextEntry zip# (ZipEntry. ~entry-name))
     ~@body
     (flush)
     (.closeEntry zip#)))

(defn jarfile [filename classes]
  (with-open [file (io/output-stream (expand-filename tmpdir filename))
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
    (get-jar-content (expand-filename tmpdir "foo.jar")) => {:jar (expand-filename tmpdir "foo.jar") :classes ["Foo" "foo.Bar"]})
  (fact "Classpath should have two entries"
    (count (get-classes-from-classpath [(expand-filename tmpdir "foo.jar") (expand-filename tmpdir "bar.jar")])) => 2))


(def project {:project "project-name"
                                   :classpath [{:jar "/tmp/foo.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}
                                             {:jar "/tmp/bar.jar" :classes ["com.bar.Kix" "com.bar.Baz"]}]})

;.;. Before the reward there must be labor. You plant before you
;.;. harvest. You sow in tears before you reap joy. -- Ransom
(with-state-changes
  [(before :facts (do
                    (reset! projects [])
                    (add-project! project)
                    ))]
  (fact "Find project by name"
    (find-project "project-name") => project)
  (facts "Filter repository by predicate"
    (find-class "project-name" "Baz") => ["com.foo.Baz" "com.bar.Baz"]
    (find-class "project-name" "Ba") => ["com.foo.Bar" "com.foo.Baz" "com.bar.Baz"]))

(facts "Should match class name starts with."
  (name-starts-with? "Baz" "com.foo.Baz") => nil
  (name-starts-with? "Baz" "com.foo.BazBar") => "com.foo.BazBar"
  (name-starts-with? "Baz" "com.foo.FooBaz") => nil)

(facts "Should match class name exactly."
  (has-name? "Baz" "com.foo.Baz") => "com.foo.Baz"
  (has-name? "Baz" "com.foo.BazBar") => nil
  (has-name? "Baz" "com.foo.FooBaz") => nil)


(fact "Should make a project which contains project name,
        classpath and classes"
  (make-project "/path/toproject/project-name") => {:project "project-name"
                                              :classpath
                                              [{:jar "/m2_repo/gid/aid/1.0/aid-1.0.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}]}
  (provided
    (mvn/read-project-info "/path/toproject/project-name")
    => {:project "project-name" :classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"]}
    (get-classes-from-classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"]) =>
      [{:jar "/m2_repo/gid/aid/1.0/aid-1.0.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}]))



(def the-classes [{:jar "aid-1.0.jar" :classes ["com.foo.Bar" "com.foo.Baz"]}])
(def the-classpath ["/m2_repo/gid/aid/1.0/aid-1.0.jar"])

(fact "Limit output to project name and classpath"
  (remove-classes-from-output {:project "project-name"
                 :classpath the-classpath
                 :classes the-classes}) => {:project "project-name" :classpath the-classpath})
