(ns altar.devices.korg.nanoktrl2
  (:require [overtone.midi :as midi]))

(defn nanoktrl2
  ([] (nanoktrl2 "nanoKONTROL2"))
  ([port-name]
    { :type          :midi
      :port-name-in  port-name
      :port-name-out port-name
      :verbs (fn [out] {}) }))
