(ns altar.controls.button
  (:require [overtone.midi :as midi])
  (:require [clojure.data :refer [diff]]))


; MIDI IO

(def mm1-in (midi/midi-in "MM-1"))

(def mm1-out (midi/midi-out "MM-1"))


; Controller-specific stuff

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


; Momentary button function

(defn momentary
  [input handler-on handler-off]
  {:pre [(not (contains? input :command))]}  ; command is implicit
  (fn button-handler [msg]
    (println "momentary")
    (when (nil? (first (diff (conj input {:command :note-on}) msg)))
      (handler-on msg))
    (when (nil? (first (diff (conj input {:command :note-off}) msg)))
      (handler-off msg))
    (msg)))

(defn toggle
  [input handler-on handler-off]
  {:pre [(not (contains? input :command))]}  ; command is implicit
  (fn button-handler [msg]
    (println "toggle")
    (when (nil? (first (diff (conj input {:command :note-on}) msg)))
      (handler-on msg))
    (when (nil? (first (diff (conj input {:command :note-off}) msg)))
      (handler-off msg))
    (msg)))


; MIDI event handler

; (defn midi-handlers
;   [& controls]
;   (fn [midi-msg]
;     (loop [controls controls]
;       (when-not (empty? controls)
;         ((first controls) midi-msg)  ; call topmost control fn with midi msg
;         (recur (rest controls))))))

(defn midi-handlers
  [& controls]
  (fn [midi-msg]
    (doall (map #(% midi-msg) controls))))


; Connect handler and controls to MIDI event

(def receiver
  (midi/midi-handle-events
    mm1-in
    (midi-handlers
      (momentary {:channel 4 :data1 19} mm1-on mm1-off)
      (momentary {:channel 4 :data1 20} mm1-on mm1-off)
      (momentary {:channel 4 :data1 23} mm1-on mm1-off)
      (momentary {:channel 4 :data1 24} mm1-on mm1-off)
      (momentary {:channel 4 :data1 27} mm1-on mm1-off)
      (momentary {:channel 4 :data1 28} mm1-on mm1-off)
      (momentary {:channel 4 :data1 31} mm1-on mm1-off)
      (momentary {:channel 4 :data1 32} mm1-on mm1-off))))