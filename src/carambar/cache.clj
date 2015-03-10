(ns carambar.cache)


(def cache (atom []))


(defn add-entry
  "Add cache entry"
  [path]
  (swap! cache conj {:entry path}))

(defn update-entry
  "Update cache entry"
  [path]
  @cache)
