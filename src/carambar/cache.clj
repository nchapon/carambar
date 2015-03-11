(ns carambar.cache)


(def cache (atom []))

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

(defn create-entry
  "Create entry"
  [path]
  {:name path
   :values []})

(defn update-entry
  "Update entry"
  [entry value]
  (update-in entry [:values] into value))

(defn parse
  "doc-string"
  [jar]
  (let [z (java.util.zip.ZipFile. jar)]
    (-> jar
        create-entry
        (update-entry (map #(.getName %) (entries z))))))

(defn find
  "Find CLASS from cache"
  [class]
  @cache)

(defn create-cache []
  (for [path (clojure.string/split (System/getProperty "sun.boot.class.path") #":")]
    (try (add-entry (parse path))
         (catch Exception e path))))
