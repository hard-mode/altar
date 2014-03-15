(ns altar.examples.mm1
  (:require [clojure.tools.namespace.repl :refer [disable-reload!]])
  (:require [overtone.midi :as midi])
  (:require [altar.controls.button :refer [momentary toggle one-of-many]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map mm1-in mm1-on mm1-off]])
  (:require [altar.utils.midi :refer [midi-match midi-handler-flat]]))

(disable-reload!)

(defn mm1-momentary [input]
  (momentary input mm1-on mm1-off))

(defn mm1-toggle [state input]
  (toggle state input mm1-on mm1-off))


(def page1 `[(mm1-momentary   (-> mm1-map :track-a :btn-1))
             (mm1-momentary   (-> mm1-map :track-a :btn-2))
             (mm1-momentary   (-> mm1-map :track-b :btn-1))
             (mm1-momentary   (-> mm1-map :track-b :btn-2))
             (mm1-momentary   (-> mm1-map :track-c :btn-1))
             (mm1-momentary   (-> mm1-map :track-c :btn-2))
             (mm1-momentary   (-> mm1-map :track-d :btn-1))
             (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(def page2 `[(mm1-toggle :on  (-> mm1-map :track-a :btn-1))
             (mm1-toggle :on  (-> mm1-map :track-a :btn-2))
             (mm1-toggle :on  (-> mm1-map :track-b :btn-1))
             (mm1-toggle :on  (-> mm1-map :track-b :btn-2))
             (mm1-momentary   (-> mm1-map :track-c :btn-1))
             (mm1-momentary   (-> mm1-map :track-c :btn-2))
             (mm1-momentary   (-> mm1-map :track-d :btn-1))
             (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(def page3 `[(mm1-toggle :off (-> mm1-map :track-a :btn-1))
             (mm1-toggle :off (-> mm1-map :track-a :btn-2))
             (mm1-toggle :on  (-> mm1-map :track-b :btn-1))
             (mm1-toggle :off (-> mm1-map :track-b :btn-2))
             (mm1-toggle :off (-> mm1-map :track-c :btn-1))
             (mm1-toggle :on  (-> mm1-map :track-c :btn-2))
             (mm1-toggle :off (-> mm1-map :track-d :btn-1))
             (mm1-toggle :on  (-> mm1-map :track-d :btn-2))])

(def page4 `[(mm1-momentary   (-> mm1-map :track-a :btn-1))
             (mm1-momentary   (-> mm1-map :track-a :btn-2))
             (mm1-momentary   (-> mm1-map :track-b :btn-1))
             (mm1-momentary   (-> mm1-map :track-b :btn-2))
             (mm1-momentary   (-> mm1-map :track-c :btn-1))
             (mm1-momentary   (-> mm1-map :track-c :btn-2))
             (mm1-momentary   (-> mm1-map :track-d :btn-1))
             (mm1-momentary   (-> mm1-map :track-d :btn-2))])

(defmacro show-page
  [page]
  `(map #(eval %) ~page))

(def controls* (atom [
  (one-of-many 0
               [(-> mm1-map :track-a :btn-cue)
                (-> mm1-map :track-b :btn-cue)
                (-> mm1-map :track-c :btn-cue)
                (-> mm1-map :track-d :btn-cue)]
               (fn [msg]
                (mm1-on msg)
                (println msg))
               mm1-off)]))

(def receiver
  (midi/midi-handle-events
    mm1-in
    (midi-handler-flat controls*)))