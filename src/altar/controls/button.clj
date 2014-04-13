(ns altar.controls.button
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


; Momentary button

(defn momentary-init! [match output] ((:off output) match))

(defn momentary-handle! [match output]
  (fn [msg]
    (when (midi-match match msg)
      (when (= (:command msg) :note-on) ((:on output) msg))
      (when (= (:command msg) :note-off) ((:off output) msg))
      (momentary-handle! match output))))

(defn momentary [match output]
  (momentary-init! match output)
  (momentary-handle! match output))


; Toggle button

(def ^:const toggle-state {:on :off
                           :off :on})

(defn toggle-init! [state match output]
  (when (= state :on) ((:on output) match))
  (when (= state :off) ((:off output) match)))

(defn toggle-handle! [state match output]
  (fn [msg]
    (let [matched (midi-match (conj match {:command :note-on}) msg)
          new-state (toggle-state state)]
      (toggle-handle!
        (if matched (do ((new-state output) msg) new-state) state)
        match output))))

(defn toggle [state match output]
  (toggle-init! state match output)
  (toggle-handle! state match output))


; One of many

(defn one-of-many- [state inputs turn-on turn-off]
  (fn [msg]
    (let [matches (filter #(midi-match (conj % {:command :note-on}) msg) inputs)]
      (when-not (empty? matches)
        (doall (map turn-off inputs))
        (turn-on msg)))  ; there can only be one match anyway
    (one-of-many- state inputs turn-on turn-off)))

(defn one-of-many
  [state inputs turn-on turn-off]
  {:pre [(vector? inputs)]}
  (doall (map turn-off inputs))
  (turn-on (nth inputs state))
  (one-of-many- state inputs turn-on turn-off))