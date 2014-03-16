(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :as midi])
  (:require [altar.devices.behringer.mm1 :refer [get-mm1-in]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

(defn fx [x]
  (println (str "side effect of " x))
  x)

(defn momentary- [input turn-on turn-off]
  (fn [msg] (when (midi-match (conj input {:command :note-on}) msg) (turn-on msg))
            (when (midi-match (conj input {:command :note-off}) msg) (turn-off msg))
    (momentary- input turn-on turn-off)))

(defn momentary
  [input turn-on turn-off]
  {:pre [(not (contains? input :command))]}
  
  (turn-off input)
  (momentary- input turn-on turn-off))

(midi/midi-handle-events (get-mm1-in) (fn [msg] (println msg)))