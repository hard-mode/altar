(ns altar.devices.behringer.lc1
  (:require [overtone.midi :as midi]))

(def ^:const lc1-channel 7)

(defn- lc1-cc [^Integer n]
  {:channel lc1-channel :data1 n :command :control-change})

(defn- lc1-note-on [^Integer n]
  {:channel lc1-channel :data1 n :command :note-on})

(def ^:const lc1-map
  {:encoders (map lc1-cc (range 16 24))
   :numbers (map lc1-note-on (range 16 24))
   :pads (map lc1-note-on (range 32 64))
   :mutes (map lc1-note-on (range 64 68))
   :solos (map lc1-note-on (range 68 72))
   :recs (map lc1-note-on (range 72 76))})

(def lc1-buttons (lazy-cat (:numbers lc1-map) (:mutes lc1-map)
                           (:solos lc1-map) (:recs lc1-map)))

(def lc1-pads (:pads lc1-map))

(defn get-lc1-in [] (midi/midi-in "CMD LC-1"))

(defn get-lc1-out [] (midi/midi-out "CMD LC-1"))

(defn get-lc1-verbs [lc1-out]
  (let [lc1-pads (:pads lc1-map)
        lc1-buttons (lazy-cat (:numbers lc1-map) (:mutes lc1-map)
                              (:solos lc1-map) (:recs lc1-map))]
    {:off (fn lc1-off [msg]
            (if (some #(= (:data1 msg) %) lc1-buttons)
              (do (midi/midi-note-on lc1-out (:data1 msg) 0 lc1-channel) msg)
              (if (some #(= (:data1 msg) %) lc1-pads)
                (do (midi/midi-note-on lc1-out (:data1 msg) 127 lc1-channel) msg)
                false)))

     :on (fn lc1-on [msg] 
           (if (some #(= (:data1 msg %)) lc1-buttons)
             (do (midi/midi-note-on lc1-out (:data1 msg) 1 lc1-channel) msg)
             (if (some #(= (:data1 msg %)) lc1-pads)
               (do (midi/midi-note-on lc1-out (:data1 msg) 0 lc1-channel) msg)
               false)))}))