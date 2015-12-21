(ns carambar.compile
  (:import (org.eclipse.jdt.core.compiler.batch BatchCompiler)))


(defn ecj-compile
  "ECJ Compilation"
  [class]
  (let [pout (java.io.PrintWriter. System/out)
        perr (java.io.PrintWriter. System/err)]
    (BatchCompiler/compile "-classpath rt.jar test_projects/simple/src/main/java/org/carambar/App.java" pout perr nil)))
