(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]]))

(defn side [x]
  (println (str "side effect of " x))
  x)

(defmacro or-list [children]
  `(or ~@children))

; Control message handlers

(defn get-handler
  ([match] (get-handler match []))
  ([match subhandlers]
    (println "initializing handler for" match)
    (fn handler [msg]
      (println "msg is" msg "; matching" match "?" (= msg match))
      (if (= msg match)
        (do (println "captured by" match) true)
        (do (println "passing to children of" match)
          (loop [i subhandlers]
            (println \newline "remaining subhandlers:" i "empty" (empty? i)
                     \newline "next subh" (first i)
                     \newline "rest subh" (rest i) \newline)
            (if (empty? i)
              (do (println "empty") nil)
              (do
                (println "evaluating" (first i))
                (if ((eval (first i)) msg)
                  true
                  (recur (rest i)))))))))))


(println ((get-handler "0" '[(get-handler "a" '[(get-handler "b")
                                                (get-handler "c")])
                             (get-handler "d")]) "d"))