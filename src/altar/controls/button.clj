(ns altar.controls.button
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


; Momentary button

(defn momentary-init! [match output] ((:off output) match))

(defn momentary-handler [match output]
  (fn ! [msg]
    (when (midi-match match msg)
      (when (= (:command msg) :note-on) ((:on output) msg))
      (when (= (:command msg) :note-off) ((:off output) msg))
      (momentary-handler match output))))

(defn momentary [match output]
  (momentary-init! match output)
  (momentary-handler match output))


; Toggle button

(def ^:const toggle-state {:on :off
                           :off :on})

(defn toggle-init! [state match output]
  (when (= state :on) ((:on output) match))
  (when (= state :off) ((:off output) match)))

(defn toggle-handler [state match output]
  (fn ! [msg]
    (let [matched (midi-match (conj match {:command :note-on}) msg)
          new-state (toggle-state state)]
      (toggle-handler
        (if matched (do ((new-state output) msg) new-state) state)
        match output))))

(defn toggle [state match output]
  (toggle-init! state match output)
  (toggle-handler state match output))


; One of many

(defn oneofmany
  ([verbs many] (oneofmany verbs 0 many))
  ([verbs initial-state many]
    (doseq [c many] ((verbs :off) c))
    ((verbs :on) (nth many initial-state))
    (fn ! [msg]
      (let [matched-state (first (filter (complement nil?)
              (for [p (map-indexed vector many)]
                (if (midi-match (assoc (second p) :command :note-on) msg)
                  (first p) nil))))
            new-state (if (nil? matched-state) initial-state matched-state)]
        (oneofmany verbs new-state many)))))