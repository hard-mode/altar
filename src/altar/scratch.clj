(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.system :refer [defsystem]])
  (:require [altar.controls.button :refer [momentary toggle oneofmany buttonbar]])
  (:require [altar.controls.page :refer [pages group]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))


; i ne zabravqi, tati, da si importnesh bibliotekite


; Utilities

(def dummy-verbs {:on  (fn [msg] (print "\n=> on"  msg))
                  :off (fn [msg] (print "\n=> off" msg))})



; Scratch

(defn n [x] (-> lc1-map :numbers (nth (- x 1))))

(defn page-momentaries [verbs]
  (apply group (for [x (range 0 32)]
    (momentary verbs (-> lc1-map :pads (nth x))))))

  ; (doall (for [x (range 0 32)] (momentary (-> lc1-map :pads (nth x)) verbs))))

(defn page-toggles [verbs]
  (apply group (for [x (range 0 32)]
    (toggle verbs (-> lc1-map :pads (nth x)) (if (even? x) :on :off)))))

(defn page-oneofmany [verbs]
  (apply group (for [x (range 0 8)]
    (oneofmany verbs (for [y (range 0 4)] {:command :note-on 
                                           :data1 (+ 32 y (* x 4))}) 1))))

(defn page-buttonbars [verbs]
  (apply group (for [x (range 0 8)]
    (buttonbar verbs (for [y (range 0 4)] {:command :note-on
                                           :data1 (+ 32 y (* x 4))}) 2))))


; (pages dummy-verbs [(n 1) page-momentaries  (n 2) page-toggles
;                     (n 3) page-oneofmany    (n 4) page-buttonbars] 1)
; (buttonbar dummy-verbs (map n (range 5 9)) 2)

(defn n [x] (-> lc1-map :numbers (nth (- x 1))))


(defsystem "Test Project"
  :controllers  [:midi "LC-1"]
  :controls     [])