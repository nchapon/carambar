(ns carambar.repository)


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

(defn add-entry
  "Add repo entry"
  [entry]
  (swap! repo conj entry))

(defn parse
  "doc-string"
  [jar]
  (let [z (java.util.zip.ZipFile. jar)]
    {:artifactid jar
     :classes (map #(filename->javaclass (.getName %)) (entries z))}))


(defn match-class-exactly?
  [re s]
  (if-not (empty? (re-find (re-pattern (str ".*\\." re "$")) s))
    true
    false))

(defn match-class?
  [re s]
  (if-not (empty? (re-find (re-pattern (str ".*\\." re ".+")) s))
    true
    false))

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
   (filter-repo-by :classes #(match-class-exactly? name %))
   (filter-repo-by :classes #(match-class? name %))))

(defn create-cache []
  (for [path (filter #(.endsWith % ".jar") boot-classpath)]
    (try (add-entry (parse path))
         (catch Exception e path))))
