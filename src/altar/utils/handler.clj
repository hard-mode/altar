(ns altar.utils.handler)

(defn get-handler
  ([match] (get-handler match []))
  ([match subhandlers]
    (fn handler [msg]
      (if (= msg match)
        true
        (loop [i subhandlers]
          (if (empty? i)
            nil
            (if ((eval (first i)) msg)
              true
              (recur (rest i)))))))))