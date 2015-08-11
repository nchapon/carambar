(ns carambar.system)


(defn get-os-name
  "Returns os-name"
  []
  (subs (.toLowerCase (System/getProperty "os.name")) 0 3))

(defmulti mvn-command get-os-name)

(defmethod mvn-command "win" []
  "mvn.bat")

(defmethod mvn-command :default []
  "mvn")
