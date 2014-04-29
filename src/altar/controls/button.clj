(ns altar.controls.button
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


(def ^:const toggle-state {:on :off
                           :off :on})


(defn btn-push
  "Lights up when pressed. "
  ([mask] (btn-push mask :off :off))
  ([mask state] (btn-push mask state state))
  ([mask base-state current-state]
    {:fn (fn ! [msg]
      (let [on         (midi-match (assoc mask :command :note-on)  msg)
            off        (midi-match (assoc mask :command :note-off) msg)
            next-state (if on (toggle-state base-state)
                         (if off base-state
                           current-state))]
        (btn-push mask base-state next-state)))
     :output (assoc mask :verb current-state)
     :state current-state}))


(defn btn-switch
  "Toggles between two states. "
  ([mask] (btn-switch mask :off))
  ([mask state]
    {:fn (fn ! [msg]
      (let [match      (midi-match (assoc mask :command :note-on) msg)
            next-state (if match (toggle-state state) state)]
        (btn-switch mask next-state)))
     :output (assoc mask :verb state)
     :state state}))


(defn btn-lazy
  "Toggles between two states on release. "
  ([mask] (btn-lazy mask :off false))
  ([mask state] (btn-lazy mask :off false))
  ([mask state waiting]
    {:fn (fn ! [msg]
      (let [next-waiting (midi-match (assoc mask :command :note-on)  msg)
            match        (midi-match (assoc mask :command :note-off) msg)
            next-state   (if (and match waiting) (toggle-state state) state)]
        (btn-lazy mask next-state next-waiting)))
     :output (assoc mask :verb state)
     :state state}))


; (defn momentary
;   "Lights up when pressed. "
;   ([verbs mask] (momentary verbs mask :off))
;   ([verbs mask initial-state]
;     ((verbs initial-state) mask)
;     {:fn (fn ! [msg]
;       (let [matched (midi-match mask msg)
;             on-state (assoc mask :command :note-on)
;             next-state (if (midi-match on-state msg)
;                         (toggle-state initial-state)
;                         initial-state)]
;         (momentary verbs mask next-state)))}))


; (defn toggle
;   "Toggles between on and off states. "
;   ([verbs mask] (toggle verbs mask :off))
;   ([verbs mask initial-state]
;     ((verbs initial-state) mask)
;     (fn ! [msg]
;       (let [matched (midi-match (conj mask {:command :note-on}) msg)
;             next-state (if matched (toggle-state initial-state) initial-state)]
;         (toggle verbs mask next-state)))))


; (defn oneofmany-render!
;   "Also used by buttonbar and pages. "
;   [cmp verbs many initial-state]
;   (doseq [c (map-indexed vector many)]
;     ((verbs (if (cmp initial-state (first c)) :on :off)) (second c))))


; (defn oneofmany-update-
;   "Also used by buttonbar. "
;   [f verbs many initial-state msg]
;     (let [next-state (or
;            (first (filter (complement nil?)
;              (for [p (map-indexed vector many)]
;                (if (midi-match (assoc (second p) :command :note-on) msg)
;                  (first p) nil))))
;            initial-state)]
;       (f verbs many next-state)))


; (defn oneofmany
;   "Only one can be on at a given time. "
;   ([verbs many] (oneofmany verbs many 0))
;   ([verbs many initial-state]
;     (oneofmany-render! = verbs many initial-state)
;     (partial oneofmany-update- oneofmany verbs many initial-state)))


; (defn buttonbar
;   "Buttons up to and including the one pressed light up. "
;   ([verbs many] (buttonbar verbs many 0))
;   ([verbs many initial-state]
;     (println "btnbar" "initial" initial-state "many" many)
;     (oneofmany-render! >= verbs many initial-state)
;     (partial oneofmany-update- buttonbar verbs many initial-state)))