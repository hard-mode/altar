(ns altar.scratch
  (:require [overtone.midi :refer
    [midi-msg midi-in midi-out midi-handle-events
     midi-note-on midi-note-off]])

  (:require [altar.system :refer [defsystem]])

  (:require [altar.devices.novation.lpd-mini :refer [lpd-mini]])
  (:require [altar.devices.korg.nanoktrl2    :refer [nanoktrl2]])

  (:require [altar.controls.button :refer
    [btn-push btn-switch btn-lazy btn-select]])
  (:require [altar.controls.page :refer [group]]))
