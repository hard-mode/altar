(ns altar.devices.novation.lpd-mini
  (:require [overtone.midi :as midi]))

(defn lpd-mini
  ([] (lpd-mini "nanoKONTROL2"))
  ([port-name]
    { :type          :midi
      :port-name-in  port-name
      :port-name-out port-name
      :verbs (fn [out] {}) }))
