(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [clj-ns-browser.sdoc :refer [sdoc]])
  
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])

  (:require [altar.system :refer [defsystem]])
  (:require [altar.devices.generic :refer [generic-midi]])
  (:require [altar.devices.behringer.lc1 :refer [lc1 lc1-n lc1-p]])
  (:require [altar.controls.button :refer [btn-push btn-switch btn-lazy]])
  (:require [altar.controls.page :refer [group]]))


(defsystem "Test Project"
  :controllers  [(lc1)]
  :controls     [(btn-push (lc1-n 1))    (btn-push (lc1-n 2))
                 (btn-push (lc1-n 5))    (btn-push (lc1-n 6))
                 (btn-switch (lc1-n 3))  (btn-switch (lc1-n 7))
                 (btn-lazy (lc1-n 4))    (btn-lazy (lc1-n 8))
                 (btn-push (lc1-p 1))])