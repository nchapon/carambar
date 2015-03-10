(ns carambar.cache)


(def cache (atom []))


(defn add-entry
  "Add cache entry"
  [entry]
  (swap! cache conj entry))

(defn create-entry
  "Update cache entry"
  [path]
  {:name path
   :values []})

(defn update-entry
  "Update entry"
  [entry value]
  (update-in entry [:values] conj value))
