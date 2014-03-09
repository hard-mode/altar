(ns altar.examples.mm1-example
  (:require [overtone.midi :as midi])
  (:require [altar.controls.button :refer [momentary toggle one-of-many]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map mm1-in mm1-on mm1-off]])
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))


(defn mm1-momentary [input]
  (momentary input mm1-on mm1-off))

(defn mm1-toggle [state input]
  (toggle state input mm1-on mm1-off))


(defn page1 [] [(mm1-momentary   (-> mm1-map :track-a :btn-1))
                (mm1-momentary   (-> mm1-map :track-a :btn-2))
                (mm1-momentary   (-> mm1-map :track-b :btn-1))
                (mm1-momentary   (-> mm1-map :track-b :btn-2))
                (mm1-momentary   (-> mm1-map :track-c :btn-1))
                (mm1-momentary   (-> mm1-map :track-c :btn-2))
                (mm1-momentary   (-> mm1-map :track-d :btn-1))
                (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(defn page2 [] [(mm1-toggle :on  (-> mm1-map :track-a :btn-1))
                (mm1-toggle :on  (-> mm1-map :track-a :btn-2))
                (mm1-toggle :on  (-> mm1-map :track-b :btn-1))
                (mm1-toggle :on  (-> mm1-map :track-b :btn-2))
                (mm1-momentary   (-> mm1-map :track-c :btn-1))
                (mm1-momentary   (-> mm1-map :track-c :btn-2))
                (mm1-momentary   (-> mm1-map :track-d :btn-1))
                (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(defn page3 [] [(mm1-toggle :off (-> mm1-map :track-a :btn-1))
                (mm1-toggle :off (-> mm1-map :track-a :btn-2))
                (mm1-toggle :on  (-> mm1-map :track-b :btn-1))
                (mm1-toggle :off (-> mm1-map :track-b :btn-2))
                (mm1-toggle :off (-> mm1-map :track-c :btn-1))
                (mm1-toggle :on  (-> mm1-map :track-c :btn-2))
                (mm1-toggle :off (-> mm1-map :track-d :btn-1))
                (mm1-toggle :on  (-> mm1-map :track-d :btn-2))])

(defn page4 [] [(mm1-momentary   (-> mm1-map :track-a :btn-1))
                (mm1-momentary   (-> mm1-map :track-a :btn-2))
                (mm1-momentary   (-> mm1-map :track-b :btn-1))
                (mm1-momentary   (-> mm1-map :track-b :btn-2))
                (mm1-momentary   (-> mm1-map :track-c :btn-1))
                (mm1-momentary   (-> mm1-map :track-c :btn-2))
                (mm1-momentary   (-> mm1-map :track-d :btn-1))
                (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(defn page-switcher [input]
  (mm1-on input))

(def controls* (atom [
  (one-of-many 0
               [(-> mm1-map :track-a :btn-cue)
                (-> mm1-map :track-b :btn-cue)
                (-> mm1-map :track-c :btn-cue)
                (-> mm1-map :track-d :btn-cue)]
               page-switcher
               mm1-off)]))

(def receiver
  (midi/midi-handle-events
    mm1-in
    (midi-handler-flat controls*)))