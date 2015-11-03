(ns carambar.repository
  (:require [carambar.mvn :as mvn]))

(def projects (atom []))

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

(defn add-project!
  "Add project"
  [p]
  (swap! projects conj p))

(defn get-jar-content
  "doc-string"
  [jar]
  (let [z (java.util.zip.ZipFile. jar)]
    {:jar jar
     :classes (map #(filename->javaclass (.getName %)) (entries z))}))


(defn has-name?
  [re s]
  (re-matches (re-pattern (str ".*\\." re "$")) s))

(defn name-starts-with?
  [re s]
  (re-matches (re-pattern (str ".*\\." re ".+")) s))

(defn filter-by
  "Filter COLL by KEY-FN and PRED"
  [key-fn pred coll]
  (reduce (fn [items i]
            (into items (filter pred (key-fn i))))
          []
          coll))

(defn has-project-name?
  "has P"
  [name s]
  (= name (:project s)))

(defn find-project
  [p-name]
  (first (filter (partial has-project-name? p-name) @projects)))

(defn find-class
  "Find CLASSNAME from repo"
  [p-name c-name]
  (let [project (find-project p-name)
        classpath (:classpath project)]
    (concat
     (filter-by :classes (partial has-name? c-name) classpath)
     (filter-by :classes (partial name-starts-with? c-name) classpath))))


(defn get-classes-from-classpath [cp]
  (for [jarfile (filter #(.endsWith % ".jar") cp)]
    (try (get-jar-content jarfile)
       (catch Exception e jarfile))))

(defn make-project
  "Add project from PATH"
  [path]
  (let [pi (mvn/read-project-info path)]
    (update-in pi [:classpath] get-classes-from-classpath)))

(defn remove-classes-from-output
  "Remove classes from output"
  [m]
  (dissoc m :classes))


(defn add-project
  "Add project from PATH"
  [path]
  (-> path
      (make-project)
      (add-project!)))

(defn status
  "Carambar status"
  []
  {:status "Running"
   :boot-classpath boot-classpath
   :mvn-repository mvn/local-repo
   :projects (map :project @projects)})
