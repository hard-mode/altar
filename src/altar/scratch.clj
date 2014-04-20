(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.controls.button :refer [momentary toggle oneofmany buttonbar]])
  (:require [altar.controls.page :refer [pages page-keys-]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))


; i ne zabravqi, tati, da si importnesh bibliotekite


; Utilities

(def dummy-verbs {:on  (fn [msg] (print "\n=> on"  msg))
                  :off (fn [msg] (print "\n=> off" msg))})



; Scratch

(defn n [x] (-> lc1-map :numbers (nth (- x 1))))

(defn page-momentaries [verbs]
  (doall (for [x (range 0 32)] (momentary (-> lc1-map :pads (nth x)) verbs))))

(defn page-toggles [verbs]
  (doall (for [x (range 0 32)]
    (toggle (if (even? x) :on :off) (-> lc1-map :pads (nth x)) verbs))))

(defn page-oneofmany [verbs]
  (doall (for [x (range 0 8)]
    (oneofmany verbs (for [y (range 0 4)] (+ y (* x 4))) 1))))

(defn page-buttonbars [verbs]
  (doall (for [x (range 0 8)]
    (buttonbar verbs (for [y (range 0 4)] (+ y (* x 4))) 2))))

; (def ps [(n 7) page-toggles  (n 1) page-momentaries
;          (n 5) page-toggles  (n 3) page-momentaries  (n 1) :foo])

; (def page-map (map list (take-nth 2 ps) (take-nth 2 (rest ps))))

; (def page-keys (page-keys- ps))



; Clip slot

; (defn clip-slot
;   ([] (let [c (clip-inst)] (clip-slot c)))
;   ([clip] (println clip)))



; System

(def system nil)

(defn init- [] {:in "LC-1", :out "LC-1"})

(defn start- [system]
  (let [in (midi-in (:in system)) out (midi-out (:out system))
        verbs (get-lc1-verbs out)

        n (fn [x] (-> lc1-map :numbers (nth (- x 1))))

        controls (atom [
          (pages verbs [(n 1) page-momentaries  (n 2) page-toggles
                        (n 3) page-oneofmany  (n 4) page-buttonbars])
          (buttonbar verbs (map n (range 5 9)) 1)])

        brain (fn [msg] (swap! controls (fn [c] (doall (map #(% msg) c)))))]
    {:in in, :out out, :verbs verbs, :controls controls, :brain brain
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