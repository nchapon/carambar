(ns carambar.cache)


(def cache (atom []))



(def boot-classpath (clojure.string/split (System/getProperty "sun.boot.class.path") #":"))


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
  "Add cache entry"
  [entry]
  (swap! cache conj entry))

(defn parse
  "doc-string"
  [jar]
  (let [z (java.util.zip.ZipFile. jar)]
    {:name jar
     :values (map #(filename->javaclass (.getName %)) (entries z))}))


(defn match-class-exactly?
  [re s]
  (if-not (empty? (re-find (re-pattern (str ".*\\." re "$")) s))
    true
    false))

(defn match-class?
  [re s]
  (if-not (empty? (re-find (re-pattern (str ".*\\." re)) s))
    true
    false))

(defn filter-entry
  "Filter entry by CLASS"
  [entry s]
  (let [filtered (filter #(match-class? s %)  (:values entry))]
    (when (not-empty filtered)
      (vec filtered))))

(defn find-class
  "Find CLASSNAME from cache"
  [s]
  (flatten (for [e @cache
             :let [f (filter-entry e s)]
             :when (not-empty f)]
         f)))

(defn create-cache []
  (for [path (filter #(.endsWith % ".jar") boot-classpath)]
    (try (add-entry (parse path))
         (catch Exception e path))))
