(ns altar.utils.handler
  (:require [altar.utils.midi :refer [midi-match]]))

(defn get-handler
  ([matcher match handle] (get-handler matcher match handle []))
  ([matcher match handle subhandlers]
    (fn handler [msg]
      (if (matcher match msg)
        (handle msg)
        (loop [subhandlers subhandlers]
          (if (empty? subhandlers)
            nil
            (if-let [bubble ((eval (first subhandlers)) msg)]
              bubble
              (recur (rest subhandlers)))))))))

(defn get-midi-handler
  ([match handle] (get-midi-handler match handle []))
  ([match handle subhandlers]
    (get-handler midi-match match handle subhandlers)))