(ns carambar.repository
  (:require [carambar.mvn :as mvn]))

(def repo (atom []))

(def boot-classpath (filter #(re-matches #".*lib/rt.jar" %) (clojure.string/split (System/getProperty "sun.boot.class.path") #":")))

(defn filename->javaclass
  "Converts filename to fully qualified java class name"
  [filename]
  (when-let [matches (re-matches #"(.*).class$" filename)]
    (clojure.string/replace (second matches) "/" ".")))

(defn class-file?
  "Is class file"
  [f]
  (re-matches #".*\.class$" f))

(defn entries
  "Zipfile entries"
  [zipfile]
  (filter #(class-file? (.getName %)) (enumeration-seq (.entries zipfile))))

(defn add-entry!
  "Add repo entry"
  [entry]
  (swap! repo conj entry))

(defn parse
  "doc-string"
  [jar]
  (let [z (java.util.zip.ZipFile. jar)]
    {:artifactid jar
     :classes (map #(filename->javaclass (.getName %)) (entries z))}))


(defn has-name?
  [re s]
  (re-matches (re-pattern (str ".*\\." re "$")) s))

(defn name-starts-with?
  [re s]
  (re-matches (re-pattern (str ".*\\." re ".+")) s))

(defn filter-repo-by
  "Filter repository by KEY-FN and PRED"
  [key-fn pred]
  (reduce (fn [classes x]
            (into classes (filter pred (key-fn x))))
          []
          @repo))

(defn find-class
  "Find CLASSNAME from repo"
  [name]
  (concat
   (filter-repo-by :classes (partial has-name? name))
   (filter-repo-by :classes (partial name-starts-with? name))))


(defn add-classes [cp]
  (for [path (filter #(.endsWith % ".jar") cp)]
    (try (parse path)
       (catch Exception e path))))

(defn make-project
  "Add project from PATH"
  [path]
  (let [pi (mvn/read-project-info path)
        cp (:classpath pi)]
    (assoc pi :classes (add-classes cp))))

(defn add-project
  "Add project from path"
  [path]
  (add-classes (into
                boot-classpath
                (:classpath (make-project path)))))
