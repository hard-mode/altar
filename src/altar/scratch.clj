(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.controls.button :refer [momentary toggle]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

; === Utilities ===

(defn fx [x] (println (str "side effect of " x)) x)

(defn dummy-handler [msg] (println "handled" msg) true)

; === Workspace ===

(defn lc1-momentary-pads [verbs]
  (for [x (range 0 32)]
    (momentary (-> lc1-map :pads (nth x)) verbs)))

(defn lc1-toggle-pads [verbs]
  (for [x (range 0 32)]
    (toggle :off (-> lc1-map :pads (nth x)) verbs)))

(defn pager-handler [pages verbs state]
  (fn ! [msg]
    (when (midi-match {:command :note-on} msg)
      (doseq [page pages]
        ((verbs (if (midi-match (first page) msg) :on :off)) (first page))))
      (pager-handler pages verbs state)))

(defn pager-init! [pages verbs state]
  (doseq [i (map-indexed vector pages)]
    ((verbs (if (= (first i) state) :on :off)) (first (last i)))))

(defn pager
  ([pages verbs] (pager pages verbs 0))
  ([pages verbs state]
    (pager-init! pages verbs state)
    (pager-handler pages verbs state)))

(defn oneofmany-handler [controls verbs state]
  (fn ! [msg]
    (when (midi-match {:command :note-on} msg)
      (doseq [ctrl controls]
        ((verbs (if (midi-match ctrl msg) :on :off)) ctrl)))
      (oneofmany-handler controls verbs state)))

(defn oneofmany-init! [controls verbs state]
  (doseq [i (map-indexed vector controls)]
    ((verbs (if (= (first i) state) :on :off)) (last i))))

(defn oneofmany
  ([controls verbs] (oneofmany controls verbs 0))
  ([controls verbs state]
    (oneofmany-init! controls verbs state)
    (oneofmany-handler controls verbs state)))

; === System ===

(def system nil)

(defn init- [] {:in "LC-1", :out "LC-1"})

(defn start- [system]
  (let [in (midi-in (:in system))
        out (midi-out (:out system))
        verbs (get-lc1-verbs out)
        controls (atom [(pager {(-> lc1-map :numbers (nth 0)) (lc1-toggle-pads verbs)
                               (-> lc1-map :numbers (nth 1)) (lc1-momentary-pads verbs)} verbs 1)
                        (oneofmany [(-> lc1-map :numbers (nth 4))
                                    (-> lc1-map :numbers (nth 5))] verbs 1)])
        brain (fn [msg] (swap! controls (fn [c] (doall (map #(% msg) c)))))]
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