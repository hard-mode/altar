(ns altar.devices.behringer.mm1
  (:require [overtone.midi :as midi]))

(def ^:const mm1-channel 4)

(defn- mm1-track [^Integer n]
  {:btn-1 {:channel mm1-channel :data1 (+ (* n 4) 19)}
   :btn-2 {:channel mm1-channel :data1 (+ (* n 4) 20)}
   :btn-cue {:channel mm1-channel :data1 (+ n 48)}})

(def ^:const mm1-map
  {:track-a (mm1-track 0)
   :track-b (mm1-track 1)
   :track-c (mm1-track 2)
   :track-d (mm1-track 3)})

(defn get-mm1-in [] (midi/midi-in "CMD MM-1"))

(defn get-mm1-out [] (midi/midi-out "CMD MM-1"))

(defn get-mm1-verbs [mm1-out]
  {:on (fn mm1-on [msg] (midi/midi-note-on mm1-out (:data1 msg) 1 mm1-channel) msg)
   :off (fn mm1-off [msg] (midi/midi-note-on mm1-out (:data1 msg) 0 mm1-channel) msg)
   :blink (fn mm1-blink [msg] (midi/midi-note-on mm1-out (:data1 msg) 2 mm1-channel) msg)})