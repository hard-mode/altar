(ns altar.utils.handler
  (:require [altar.utils.midi :refer [midi-match]]))

(defn get-handler
  ([match matcher handle] (get-handler match matcher handle []))
  ([match matcher handle subhandlers]
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
  ([match handle] (get-handler match midi-match handle []))
  ([match handle subhandlers]
    (println "initializing midi handler" match)
    (get-handler match midi-match handle subhandlers)))