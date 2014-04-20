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


(defn oneofmany
  "Only one can be on at a given time. "
  ([verbs many] (oneofmany verbs 0 many))
  ([verbs initial-state many]
    (doseq [c many] ((verbs :off) c))
    ((verbs :on) (nth many initial-state))
    (fn ! [msg]
      (let [matched (first (filter (complement nil?)
              (for [p (map-indexed vector many)]
                (if (midi-match (assoc (second p) :command :note-on) msg)
                  (first p) nil))))
            new-state (if (nil? matched) initial-state matched)]
        (oneofmany verbs new-state many)))))