(ns altar.examples.mm1-example
  (:require [overtone.midi :as midi])
  (:require [altar.controls.button :refer [momentary toggle one-of-many]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-in mm1-on mm1-off]])
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


; Connect handler and controls to MIDI event

(def controls* (atom [(momentary {:channel 4 :data1 19} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 20} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 23} mm1-on mm1-off)
                      (momentary {:channel 4 :data1 24} mm1-on mm1-off)
                      (toggle :on {:channel 4 :data1 27} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 28} mm1-on mm1-off)
                      (toggle :on {:channel 4 :data1 31} mm1-on mm1-off)
                      (toggle :off {:channel 4 :data1 32} mm1-on mm1-off)
                      (one-of-many 0 [{:channel 4 :data1 48}
                                      {:channel 4 :data1 49}
                                      {:channel 4 :data1 50}
                                      {:channel 4 :data1 51}] mm1-on mm1-off)]))

(def receiver
  (midi/midi-handle-events
    mm1-in
    (midi-handler-flat controls*)))