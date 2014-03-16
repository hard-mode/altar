(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events]])
  (:require [altar.devices.behringer.mm1 :refer [get-mm1-in]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

; Side effect utility to explore lazy evaluation

(defn fx [x]
  (println (str "side effect of " x))
  x)

; System

(def system nil)

(defn init- []
  {:in "MM-1"
   :out "MM-1"
   :brain (fn brain [msg] (println msg))})

(defn start- [system]
  (let [in (midi-in (:in system))]
    {:in in
     :out (midi-out (:out system))
     :receiver (midi-handle-events in (:brain system))}))

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

; WIP

(defn momentary- [input turn-on turn-off]
  (fn [msg] (when (midi-match (conj input {:command :note-on}) msg) (turn-on msg))
            (when (midi-match (conj input {:command :note-off}) msg) (turn-off msg))
    (momentary- input turn-on turn-off)))

(defn momentary
  [input turn-on turn-off]
  {:pre [(not (contains? input :command))]}
  
  (turn-off input)
  (momentary- input turn-on turn-off))