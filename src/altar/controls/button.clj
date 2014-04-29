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
     :mask mask
     :state current-state
     :output [(assoc mask :verb current-state)]}))


(defn btn-switch
  "Toggles between two states. "
  ([mask] (btn-switch mask :off))
  ([mask state]
    {:fn (fn ! [msg]
      (let [match      (midi-match (assoc mask :command :note-on) msg)
            next-state (if match (toggle-state state) state)]
        (btn-switch mask next-state)))
     :mask mask
     :state state
     :output [(assoc mask :verb state)]}))


(defn btn-switch-lazy
  "Toggles between two states on release. "
  ([mask] (btn-switch-lazy mask :off false))
  ([mask state] (btn-switch-lazy mask :off false))
  ([mask state pressed]
    {:fn (fn ! [msg]
      (let [released     (midi-match (assoc mask :command :note-off) msg)
            next-pressed (or (and pressed (not released))
                             (midi-match (assoc mask :command :note-on) msg))
            next-state   (if (and pressed released) (toggle-state state) state)]
        (btn-switch-lazy mask next-state next-pressed)))
     :mask mask
     :state state
     :pressed pressed
     :output [(assoc mask :verb state)]}))


(def btn-lazy btn-switch-lazy)


(defn btn-select
  "Only one out of several can be on at a given time. "
  ([many] (btn-select many (first many)))
  ([many state]
    {:fn (fn ! [msg]
      (let [matches    (filter #(= (:state %) :on) 
                         (for [mask many] ((:fn (btn-switch mask)) msg)))
            next-state (or (get (first matches) :mask) state)]
        (btn-select many next-state)))
     :many many
     :state state
     :output (for [mask many] (assoc mask :verb (if (midi-match mask state)
                                                 :on :off)))}))


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