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


(def mm1-map
  {:track-a {:btn-1 {:channel 4 :data1 19} :btn-2 {:channel 4 :data1 20}
             :btn-cue {:channel 4 :data1 48}}
   :track-b {:btn-1 {:channel 4 :data1 23} :btn-2 {:channel 4 :data1 24}
             :btn-cue {:channel 4 :data1 49}}
   :track-c {:btn-1 {:channel 4 :data1 27} :btn-2 {:channel 4 :data1 28}
             :btn-cue {:channel 4 :data1 50}}
   :track-d {:btn-1 {:channel 4 :data1 31} :btn-2 {:channel 4 :data1 32}
             :btn-cue {:channel 4 :data1 51}}})