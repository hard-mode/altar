(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-in get-mm1-on get-mm1-off]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

; Side effect utilities to explore lazy evaluation

(defn fx [x] (println (str "side effect of " x)) x)

(defn dummy-handler [msg] (println "handled" msg) true)

; Momentary

(defn momentary-init- [match output] ((:turn-off output) match))

(defn momentary-handle- [match output]
  (fn [msg]
    (when (midi-match match msg)
      (when (= (:command msg) :note-on) (println "turning on")
                                        ((:turn-on output) msg))
      (when (= (:command msg) :note-off) (println "turning off")
                                         ((:turn-off output) msg))
      (momentary-handle- match output))))

(defn momentary [match output]
  (momentary-init- match output)
  (momentary-handle- match output))

; Handlers preparation

(defn prepare-handler [context handler-options]
  (let [output {:turn-on (get-mm1-on (:out context))
                :turn-off (get-mm1-off (:out context))}]
    (println "preparing handler" handler-options)
    (get-midi-handler
      (first handler-options)
      dummy-handler
      (map #(prepare-handler context %) (drop 2 handler-options)))))

(defn update-handler [handler msg]
  (handler msg))

; System

(def system nil)

(defn init- [] {:in "MM-1", :out "MM-1"})

(defn start- [system]
  (let [in (midi-in (:in system)) out (midi-out (:out system))
        outputs {:turn-on (get-mm1-on out)
                 :turn-off (get-mm1-off out)}
        controls (atom [(momentary (-> mm1-map :track-a :btn-1) outputs)])
        brain (fn [msg] (doall (map #(% msg) (deref controls))))]
    {:in in, :out out, :controls controls, :brain brain
     :receiver (midi-handle-events in brain)}))

(defn stop- [system]
  (.close (-> system :in :device))
  (.close (-> system :out :device))
  {:in nil :out nil})

(defn init []
  (alter-var-root #'system (constantly (init-))))

(defn start []
  (alter-var-root #'system start-))

(defn stop []
  (alter-var-root #'system stop-))

(defn go [] (init) (start))

(defn reset []
  (do (stop) (refresh))
  nil)

; :handler (atom (get-midi-handler
;                  {:channel 4 :data1 18}
;                  dummy-handler
;                  [(get-midi-handler {:channel 4 :data1 19} dummy-handler [
;                     (get-midi-handler {:channel 4 :data1 20} dummy-handler)])
;                   (get-midi-handler {:channel 4 :data1 21} dummy-handler)]))