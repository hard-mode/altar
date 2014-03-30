(ns altar.devices.behringer.lc1
  (:require [overtone.midi :as midi]))

(def ^:const lc1-channel 7)

(defn- lc1-cc [^Integer n]
  {:channel lc1-channel :data1 n :command :control-change})

(defn- lc1-note [^Integer n]
  {:channel lc1-channel :data1 n})

(def ^:const lc1-map
  {:encoders (map lc1-cc (range 16 24))
   :numbers (map lc1-note (range 16 24))
   :pads (map lc1-note (range 32 64))
   :mutes (map lc1-note (range 64 68))
   :solos (map lc1-note (range 68 72))
   :recs (map lc1-note (range 72 76))})

(defn get-lc1-in [] (midi/midi-in "CMD LC-1"))

(defn get-lc1-out [] (midi/midi-out "CMD LC-1"))

(defn get-lc1-verbs [lc1-out]
  (let [get-data1 (fn [mapping grp] (map #(:data1 %) (grp lc1-map)))
        
        lc1-pads (get-data1 lc1-out :pads)
        lc1-buttons (lazy-cat (get-data1 lc1-out :numbers) 
                              (get-data1 lc1-out :mutes)
                              (get-data1 lc1-out :solos)
                              (get-data1 lc1-out :recs))]

    {:off (fn lc1-off [msg]
            (if (some #(= (:data1 msg) %) lc1-buttons)
              (midi/midi-note-on lc1-out (:data1 msg) 0 lc1-channel)
              (if (some #(= (:data1 msg) %) lc1-pads)
                (midi/midi-note-on lc1-out (:data1 msg) 127 lc1-channel)
                false)))

     :on (fn lc1-on [msg]
           (if (some #(= (:data1 msg) %) lc1-buttons)
             (midi/midi-note-on lc1-out (:data1 msg) 1 lc1-channel)
             (if (some #(= (:data1 msg) %) lc1-pads)
               (midi/midi-note-on lc1-out (:data1 msg) 0 lc1-channel)
               false)))}))