(ns altar.scratch-old
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [clojure.pprint :refer [pprint]])
  (:require [clj-ns-browser.sdoc :refer [sdoc]])
  
  (:require [overtone.midi :refer
    [midi-msg midi-in midi-out midi-handle-events
     midi-note-on midi-note-off]])

  (:require [altar.system :refer [defsystem]])

  (:require [altar.devices.novation.lpd-mini :refer [lpd]])
  (:require [altar.devices.korg.nanoktrl2    :refer [nanoktrl2]])

  (:require [altar.controls.button :refer
    [btn-push btn-switch btn-lazy btn-select]])
  (:require [altar.controls.page :refer [group]]))

(defsystem "Test Project"
  :controllers  [(lc1)]
  :controls     [(btn-push (lc1-n 1))    (btn-push (lc1-n 2))
                 (btn-push (lc1-n 5))    (btn-push (lc1-n 6))
                 (btn-switch (lc1-n 3))  (btn-switch (lc1-n 7))
                 (btn-lazy (lc1-n 4))    (btn-lazy (lc1-n 8))
                 (btn-select [(lc1-p 1) (lc1-p 2) (lc1-p 3) (lc1-p 4)])])
