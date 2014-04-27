(ns altar.devices.behringer.lc1
  (:require [overtone.midi :refer [midi-note-on midi-note-off]]))

(def ^:const lc1-channel 7)

(defn lc1 [] "lc1")

(defn lc1-cc [^Integer n]
  {:channel lc1-channel :data1 n :command :control-change})

(defn lc1-note [^Integer n]
  {:channel lc1-channel :data1 n})

(def ^:const lc1-map
  {:encoders (map lc1-cc (range 16 24))
   :numbers (map lc1-note (range 16 24))
   :pads (map lc1-note (range 32 64))
   :mutes (map lc1-note (range 64 68))
   :solos (map lc1-note (range 68 72))
   :recs (map lc1-note (range 72 76))})

(defn lc1-e [x] (-> lc1-map :encoders (nth (- x 1))))

(defn lc1-n [x] (-> lc1-map :numbers (nth (- x 1))))

(defn lc1-p [x] (-> lc1-map :pads (nth (- x 1))))

(defn lc1-m [x] (-> lc1-map :solos (nth (- x 1))))

(defn lc1-s [x] (-> lc1-map :recs (nth (- x 1))))

(defn lc1 []
 {:type :midi
  :port-name-in "LC-1"
  :port-name-out "LC-1"
  :verbs (fn [out]
    (let [get-data1 (fn [mapping grp] (map #(:data1 %) (grp lc1-map)))
        
          lc1-pads (get-data1 out :pads)
          lc1-buttons (lazy-cat (get-data1 out :numbers) (get-data1 out :mutes)
                                (get-data1 out :solos)   (get-data1 out :recs))]
      {:on  (fn [msg]
              (if (some #(= (:data1 msg) %) lc1-buttons)
                (midi-note-on out (:data1 msg) 1 lc1-channel)
                  (if (some #(= (:data1 msg) %) lc1-pads)
                    (midi-note-on out (:data1 msg) 0 lc1-channel)
                    false)))
       :off (fn [msg]
              (if (some #(= (:data1 msg) %) lc1-buttons)
                (midi-note-on out (:data1 msg) 0 lc1-channel)
                (if (some #(= (:data1 msg) %) lc1-pads)
                  (midi-note-on out (:data1 msg) 127 lc1-channel)
                  false)))}))})