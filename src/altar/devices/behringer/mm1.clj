(ns altar.devices.behringer.mm1
  (:require [overtone.midi :as midi]))


; Controller-specific stuff

(def mm1-in (midi/midi-in "CMD MM-1"))

(def mm1-out (midi/midi-out "CMD MM-1"))

(defn mm1-off [msg]
  (println "off" (:data1 msg))
  (midi/midi-note-on mm1-out (:data1 msg) 0 4)
  msg)

(defn mm1-on [msg]
  (println "on" (:data1 msg))
  (midi/midi-note-on mm1-out (:data1 msg) 1 4)
  msg)

(defn mm1-blink [msg]
  (println "blink" (:data1 msg))
  (midi/midi-note-on mm1-out (:data1 msg) 2 4)
  msg)