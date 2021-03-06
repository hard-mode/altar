(ns altar.utils.midi
  (:require [clojure.data :refer [diff]]))


; MIDI message matcher

(defn midi-match
  [match msg]
  (nil? (first (diff match msg))))


; MIDI comparator

(defn midi-cmp [x y]
  (compare [(:data1 x) (:data2 x)]
           [(:data1 y) (:data2 y)]))


; Old MIDI event handlers

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