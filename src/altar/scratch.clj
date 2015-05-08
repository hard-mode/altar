(ns altar.scratch
  (:require
    [overtone.midi                   :refer [
      midi-msg midi-in midi-out midi-handle-events
      midi-note-on midi-note-off]]
    [altar.system                    :refer [defsystem]]
    [altar.devices.novation.lpd-mini :refer [lpd-mini]]
    [altar.devices.korg.nanoktrl2    :refer [nanoktrl2]]
    [altar.controls.button           :refer [
      btn-push btn-switch btn-lazy btn-select]]
    [altar.controls.page             :refer [group]]))

(def lpd (lpd-mini))

(def nk2 (nanoktrl2))

(defsystem "Bass and Drums"
  :controllers [lpd nk2])
