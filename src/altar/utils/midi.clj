(ns altar.utils.midi
  (:require [clojure.data :refer [diff]]))


; MIDI message matcher
; TODO: Make :note match :note-on and :note-off?

(defn midi-match [match msg]
  (nil? (first (diff match msg))))


; MIDI event handlers

(defn midi-handler-recur
  [controls]
  (fn [midi-msg]
    (loop [controls (deref controls)]
      (when-not (empty? controls)
        ((first controls) midi-msg)
        (recur (rest controls))))))

(defn midi-handler-flat
  [control-atom]
  (fn [midi-msg]
    (reset! control-atom
      (doall (map #(% midi-msg) (deref control-atom))))))