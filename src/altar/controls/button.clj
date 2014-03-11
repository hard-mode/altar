(ns altar.controls.button
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


; Momentary button

(defn momentary- [input turn-on turn-off]
  (fn [msg] (when (midi-match (conj input {:command :note-on}) msg) (turn-on msg))
            (when (midi-match (conj input {:command :note-off}) msg) (turn-off msg))
    (momentary- input turn-on turn-off)))

(defn momentary
  [input turn-on turn-off]
  {:pre [(not (contains? input :command))]}
  
  (turn-off input)
  (momentary- input turn-on turn-off))


; Toggle button

(def toggle-state {:on :off
                   :off :on})

(defn toggle- [state input turn-on turn-off]
  (fn [msg]
    (let [matched (midi-match (conj input {:command :note-on}) msg)]
      (when (and matched (= state :on)) (turn-off msg))
      (when (and matched (= state :off)) (turn-on msg))
      (toggle-
        (if matched
          (toggle-state state)
          state)
        input turn-on turn-off))))

(defn toggle
  [state input turn-on turn-off]
  {:pre [(not (contains? input :command))]}

  (when (= state :on) (turn-on input))
  (when (= state :off) (turn-off input))

  (toggle- state input turn-on turn-off))


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