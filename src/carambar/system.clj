(ns carambar.system)


(defmulti mvn-command (fn [] (subs (.toLowerCase (System/getProperty "os.name")) 0 3)))

(defmethod mvn-command "win" []
  "mvn.bat")

(defmethod mvn-command :default []
  "mvn")
