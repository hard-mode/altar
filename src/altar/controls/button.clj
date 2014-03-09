(ns altar.controls.button
  (:require [overtone.midi :as midi])
  (:require [clojure.data :refer [diff]]))


; Momentary button function

(defn momentary
  [input turn-on turn-off]
  {:pre [(not (contains? input :command))]}  ; command is implicit
  (fn button-handler [msg]
    (when (nil? (first (diff (conj input {:command :note-on}) msg))) (turn-on msg))
    (when (nil? (first (diff (conj input {:command :note-off}) msg))) (turn-off msg))
    (momentary input turn-on turn-off)))

(def toggle-state {:on :off
                   :off :on})

(defn toggle
  [state input turn-on turn-off]
  {:pre [(not (contains? input :command))]}  ; command is implicit
  (fn button-handler [msg]
    (when (nil? (first (diff (conj input {:command :note-on}) msg))))
    (when (nil? (first (diff (conj input {:command :note-off}) msg))))
    (toggle (toggle-state :off) input turn-on turn-off)))


; MIDI event handler

(defn midi-handler-recur
  [controls]
  (fn [midi-msg]
    (loop [controls (deref controls)]
      (when-not (empty? controls)
        ((first controls) midi-msg)  ; call topmost control fn with midi msg
        (recur (rest controls))))))

(defn midi-handler-flat
  [control-atom]
  (fn [midi-msg]
    (let [updated-control-atom (doall (map #(% midi-msg) (deref control-atom)))]
      (reset! control-atom updated-control-atom))))


; Controller-specific stuff

(def mm1-in (midi/midi-in "MM-1"))

(def mm1-out (midi/midi-out "MM-1"))

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

(defn mm1-toggle-press [state msg])


; Connect handler and controls to MIDI event


(def controls* (atom [(momentary {:channel 4 :data1 19} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 20} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 23} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 24} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 27} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 28} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 31} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 32} mm1-on mm1-off)]))

(def receiver
  (midi/midi-handle-events
    mm1-in
    (midi-handler-flat controls*)))