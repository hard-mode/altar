(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [altar.utils.handler :refer [get-handler]]))

(defn fx [x]
  (println (str "side effect of " x))
  x)