; i ne zabravqi, tati, da si importnesh bibliotekite

(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])

  (:require [altar.system :refer [defsystem]])
  (:require [altar.devices.generic :refer [generic-midi]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map]])
  (:require [altar.controls.button :refer [btn-push]])
  (:require [altar.controls.page :refer [group]]))


(def dummy-verbs {:on  (fn [msg] (print "\n=> on"  msg))
                  :off (fn [msg] (print "\n=> off" msg))})


(defn n [x] (-> lc1-map :numbers (nth (- x 1))))


(defsystem "Test Project"
  :controllers  [(generic-midi "LC-1")]
  :controls     [(btn-push (n 1))  (btn-push (n 2))
                 (btn-push (n 5))  (btn-push (n 6))])