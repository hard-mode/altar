(ns altar.scratch
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-msg midi-in midi-out midi-handle-events
                                   midi-note-on midi-note-off]])
  (:require [altar.controls.button :refer [momentary toggle oneofmany]])
  (:require [altar.devices.behringer.mm1 :refer [mm1-map get-mm1-verbs]])
  (:require [altar.devices.behringer.lc1 :refer [lc1-map get-lc1-verbs]])
  (:require [altar.utils.midi :refer [midi-match]])
  (:require [altar.utils.handler :refer [get-handler get-midi-handler]]))

; === Utilities ===

(def dummy-verbs {:on  (fn [msg] (print "\n=> on"  msg))
                  :off (fn [msg] (print "\n=> off" msg))})


; === LC1 mappings ===

(defn lc1-momentary-pads
  [verbs]
  (for [x (range 0 32)]
    (momentary (-> lc1-map :pads (nth x)) verbs))
  true)

(defn lc1-toggle-pads
  [verbs]
  (for [x (range 0 32)]
    (toggle (if (even? x) :on :off) (-> lc1-map :pads (nth x)) verbs)))


; === Pager ===

(defn pager-handler
  [verbs pages]
  (fn ! [msg]
    (let [page-pairs (map vector (take-nth 2 pages) (take-nth 2 (rest pages)))
          indexed-keys (map-indexed vector (take-nth 2 pages))
          matched-pages (map-indexed vector
                          (filter #(midi-match (second %) msg) page-pairs))])
      ; (println page-pairs indexed-keys matched-pages))
      (pager-handler verbs pages)))
      ; if matched, redraw
      ; (when state
      ;   (doseq [c indexed-keys] ((verbs :off) (second c)))     ; clear all
      ;   ((verbs :on) (second (nth indexed-keys state)))  ; draw one
      ;   (println page))  ; draw page

      ; return next handler
      ; (pager-handler pages verbs state))))

      ; (doseq [i indexed-pages]
      ;   (let [page-num (first i)
      ;         page-key (first (second i))
      ;         page-contents (second (second i))]
      ;     (when (midi-match page-key msg)
      ;       (doseq [c (keys pages)] ((verbs :off) c))  ; all controls off
      ;       ((verbs :on) msg)                          ; matched control on
      ;       (let [page (second (second i))]            ; load page
      ;         (println i page (apply page [verbs]))))))

(defn pager-init!
  [verbs pages])
  ; (doseq [i (map-indexed vector (apply hash-map pages))]
  ;   ((verbs (if (= (first i) state) :on :off)) (first (last i)))))

(defn pager
  ([verbs pages]
    (pager-init! verbs pages)
    (pager-handler verbs pages)))


; === System ===

(def system nil)

(defn init- [] {:in "LC-1", :out "LC-1"})

(defn start- [system]
  (let [in (midi-in (:in system))
        out (midi-out (:out system))
        verbs (get-lc1-verbs out)

        ; pager1 (partial pager verbs)
        ; oneofmany1 (partial pager oneofmany)

        controls (atom [(pager verbs [(-> lc1-map :numbers (nth 0)) lc1-toggle-pads
                                      (-> lc1-map :numbers (nth 1)) lc1-momentary-pads
                                      (-> lc1-map :numbers (nth 2)) lc1-toggle-pads
                                      (-> lc1-map :numbers (nth 3)) lc1-momentary-pads])
                        (oneofmany verbs 0 (map #(-> lc1-map :numbers (nth %)) (range 4 8)))])
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