(ns altar.core
  (:use [overtone.midi])
  (:gen-class))

(def sequencer (atom [[1 0 0 0 0 1 0 0]
                      [0 0 1 0 0 0 1 0]
                      [1 1 1 1 1 1 1 0]
                      [0 0 0 0 0 0 0 1]]))

(def sequencer-active-track (atom 0))

(def mm1-in (midi-in "MM-1"))

(def mm1-out (midi-out "MM-1"))

(defn turn-off [buttons]
  (dorun
    (for [button buttons]
      (midi-note-on mm1-out button 0 4))))

(defn turn-on [buttons]
  (dorun
    (for [button buttons]
      (midi-note-on mm1-out button 1 4))))

(defn step-press [midi-msg] midi-msg)

(defn step-release [midi-msg] midi-msg)

(defn note-to-step [note])

(defn display-step [step velocity]
  (let [note (nth [19 20 23 24 27 28 31 32] step)
        channel 4]
    (midi-note-on mm1-out note velocity channel)))

(defn display-track [track-number]
  (dorun (map-indexed display-step (nth @sequencer track-number))))

(defn handler-track-btn [midi-msg]
  (turn-off #{48 49 50 51})
  (let [note (midi-msg :data1)]
    (turn-on #{note})
    (display-track (- note 48))))

(defn handler-default [midi-msg]
  (println (map midi-msg [:timestamp :command :data1 :data2])))

(defn handler-midi [midi-msg]
  (let [step-buttons #{19 20 23 24 27 28 31 32}
        step-handlers {:note-on (fn [midi-msg] (step-press (note-to-step (midi-msg :data1))))
                       :note-off (fn [midi-msg] (step-release (note-to-step (midi-msg :data1))))}]
    (when (contains? step-buttons (midi-msg :data1))
      ((or (step-handlers (midi-msg :command)) handler-default) midi-msg)))

  (let [track-buttons #{48 49 50 51}
        track-handlers {:note-on handler-track-btn}]
    (when (contains? track-buttons (midi-msg :data1))
      ((or (track-handlers (midi-msg :command)) handler-default) midi-msg))))

(def receiver (midi-handle-events mm1-in handler-midi))

(turn-off #{19 20 23 24 27 28 31 32 48 49 50 51})