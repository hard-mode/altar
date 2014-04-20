(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.controls.button :refer [momentary toggle oneofmany]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.midi :refer [midi-match midi-cmp]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))



; Utilities

(def dummy-verbs {:on  (fn [msg] (print "\n=> on"  msg))
                  :off (fn [msg] (print "\n=> off" msg))})



; Pager

(defn page-keys-
  [pages]
  (map-indexed vector (apply sorted-set-by midi-cmp (take-nth 2 pages))))

(defn page-index-
  [mask page-keys]
  (first (first (filter #(= (second %) mask) page-keys))))

(defn pager-handler
  [verbs pages initial]
  (fn ! [msg]
    (let [page-map (map list (take-nth 2 pages) (take-nth 2 (rest pages)))
          page-keys (page-keys- pages)
          matcher (fn [x] (midi-match (assoc (first x) :command :note-on) msg))
          matches (filter matcher page-map)
          matched-mask (first (map first matches))
          matched-state (page-index- matched-mask page-keys)
          matched-pages (map second matches)]
      (when (and (seq matched-pages))
        (doseq [c page-keys] ((verbs (if (= (first c) matched-state) :on :off)) (second c)))
        (println "\n matches" matches "\n matched-state" matched-state "\n matched-mask" matched-mask "\n matched-pages" matched-pages)))
    (pager-handler verbs pages initial)))

(defn pager-init!
  [verbs pages initial]
  (doseq [i (page-keys- pages)]
    ((verbs (if (= (first i) initial) :on :off)) (second i))))

(defn pager
  ([verbs pages] (pager verbs pages 0))
  ([verbs pages initial]
    (pager-init! verbs pages initial)
    (pager-handler verbs pages initial)))

(defn oom
  ([verbs many] (oom verbs 0 many))
  ([verbs initial-state many]
    (doseq [c many] ((verbs :off) c))
    ((verbs :on) (nth many initial-state))
    (fn ! [msg]
      (let [matched-state (first (filter (complement nil?)
              (for [p (map-indexed vector many)]
                (if (midi-match (assoc (second p) :command :note-on) msg)
                  (first p) nil))))
            new-state (if (nil? matched-state) initial-state matched-state)]
        (oom verbs new-state many)))))



; Scratch

(defn n [x] (-> lc1-map :numbers (nth (- x 1))))

(defn page-momentaries [verbs]
  (doall (for [x (range 0 32)] (momentary (-> lc1-map :pads (nth x)) verbs))))

(defn page-toggles [verbs]
  (doall (for [x (range 0 32)]
    (toggle (if (even? x) :on :off) (-> lc1-map :pads (nth x)) verbs))))

(def pages [(n 7) page-toggles  (n 1) page-momentaries
            (n 5) page-toggles  (n 3) page-momentaries (n 1) :foo])

(def page-map (map list (take-nth 2 pages) (take-nth 2 (rest pages))))

(def page-keys (page-keys- pages))



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

        ; pager1 (partial pager verbs)
        ; oneofmany1 (partial pager oneofmany)
        n (fn [x] (-> lc1-map :numbers (nth (- x 1))))

        controls (atom [
          (pager verbs [(n 1) page-toggles  (n 2) page-momentaries
                        (n 3) page-toggles  (n 4) page-momentaries])
          (oneofmany verbs 0 (map n (range 5 9)))])

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