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

(defn blink [buttons]
  (dorun
    (for [button buttons]
      (midi-note-on mm1-out button 2 4))))

(defn display-step [step velocity]
  (let [note (nth [19 20 23 24 27 28 31 32] step)
        channel 4]
    (midi-note-on mm1-out note velocity channel)))

(defn step-press [step] (blink [(nth [19 20 23 24 27 28 31 32] step)]))

(defn update-sequencer [sequencer track-number step value]
  (assoc sequencer track-number
    (assoc (nth sequencer track-number)
      step value)))

(defn step-release [step]
  (let [track (nth @sequencer @sequencer-active-track)
        new-step-value (- 1 (nth track step))]
    (swap! sequencer update-sequencer
      @sequencer-active-track step new-step-value)
    (display-step step new-step-value)))

(defn note-to-step [note] (.indexOf [19 20 23 24 27 28 31 32] note))

(defn display-track [track-number]
  (dorun (map-indexed display-step (nth @sequencer track-number))))

(defn handler-track-btn [midi-msg]
  (turn-off #{48 49 50 51})
  (let [note (midi-msg :data1)
        track-number (- note 48)]
    (turn-on #{note})
    (display-track track-number)
    (reset! sequencer-active-track track-number)))

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
(turn-on #{48})
(display-track 0)