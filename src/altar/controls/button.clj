(ns altar.controls.button
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


(def ^:const toggle-state {:on :off
                           :off :on})


(defn momentary
  "Lights up when pressed. "
  ([verbs mask] (momentary verbs mask :off))
  ([verbs mask initial-state]
    ((verbs initial-state) mask)
    (fn ! [msg]
      (let [matched (midi-match mask msg)
            on-state (assoc mask :command :note-on)
            new-state (if (midi-match on-state msg)
                        (toggle-state initial-state)
                        initial-state)]
        (momentary verbs mask new-state)))))


(defn toggle
  "Toggles between on and off states. "
  ([verbs mask] (toggle verbs mask :off))
  ([verbs mask initial-state]
    ((initial-state verbs) mask)
    (fn ! [msg]
      (let [matched (midi-match (conj mask {:command :note-on}) msg)
            new-state (if matched (toggle-state initial-state) initial-state)]
        (println matched "\n" new-state)
        (toggle verbs mask new-state)))))


(defn oneofmany-
  "Shared by oneofmany and buttonbar. "
  [f verbs initial-state many msg]
    (let [matched (first (filter (complement nil?)
            (for [p (map-indexed vector many)]
              (if (midi-match (assoc (second p) :command :note-on) msg)
                (first p) nil))))
          new-state (if (nil? matched) initial-state matched)]
      (f verbs new-state many)))


(defn oneofmany
  "Only one can be on at a given time. "
  ([verbs many] (oneofmany verbs 0 many))
  ([verbs initial-state many]
    (doseq [c (map-indexed vector many)]
      ((verbs (if (= initial-state (first c)) :on :off)) (second c)))
    (partial oneofmany- oneofmany verbs initial-state many)))


(defn buttonbar
  "Same as above, but controls up to and including the active on light up. "
  ([verbs many] (buttonbar verbs 0 many))
  ([verbs initial-state many]
    (doseq [c (map-indexed vector many)]
      ((verbs (if (>= initial-state (first c)) :on :off)) (second c)))
    (partial oneofmany- buttonbar verbs initial-state many)))