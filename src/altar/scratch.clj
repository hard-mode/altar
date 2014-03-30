(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.controls.button :refer [momentary]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

; === Utilities ===

(defn fx [x] (println (str "side effect of " x)) x)

(defn dummy-handler [msg] (println "handled" msg) true)

; === Workspace ===

; === System ===

(def system nil)

(defn init- [] {:in "LC-1", :out "LC-1"})

(defn start- [system]
  (let [in (midi-in (:in system))
        out (midi-out (:out system))
        verbs (get-lc1-verbs out)
        controls (atom [(momentary (-> lc1-map :pads (nth 0)) verbs)
                        (momentary (-> lc1-map :pads (nth 1)) verbs)
                        (momentary (-> lc1-map :pads (nth 2)) verbs)
                        (momentary (-> lc1-map :pads (nth 3)) verbs)])
        brain (fn [msg] (doall (map #(% msg) (deref controls))))]
    {:in in, :out out,
     :verbs verbs, :controls controls, :brain brain
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

(defn reset [] (do (stop) (refresh)) nil)