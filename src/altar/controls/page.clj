(ns altar.controls.page
  (:require [altar.controls.button :refer [oneofmany-update- oneofmany-render!]])
  (:require [altar.utils.midi :refer [midi-match midi-cmp]]))


(defn page-keys-
  [page-list]
  (apply sorted-set-by midi-cmp (take-nth 2 page-list)))


(defn indexed-page-keys-
  [page-list]
  (map-indexed vector (apply sorted-set-by midi-cmp (take-nth 2 page-list))))


(defn page-index-
  [mask page-keys]
  (first (first (filter #(= (second %) mask) page-keys))))


; (defn pages-handler
;   [verbs page-list initial]
;   (fn ! [msg]
;     (let [page-map (map list (take-nth 2 page-list) (take-nth 2 (rest page-list)))
;           page-keys (page-keys- page-list)
;           matcher (fn [x] (midi-match (assoc (first x) :command :note-on) msg))
;           matches (filter matcher page-map)
;           matched-mask (first (map first matches))
;           matched-state (page-index- matched-mask page-keys)
;           matched-page-list (map second matches)]
;       (when (and (seq matched-page-list))
;         (doseq [c page-keys] ((verbs (if (= (first c) matched-state) :on :off)) (second c)))
;         (println "\n matches" matches "\n matched-state" matched-state "\n matched-mask" matched-mask "\n matched-page-list" matched-page-list)))
;     (pages-handler verbs page-list initial)))

; pisna mi da go restartiram toz interpretator.

; (defn pages-init!
;   [verbs page-list initial]
;   (doseq [i (page-keys- page-list)]
;     ((verbs (if (= (first i) initial) :on :off)) (second i))))


(defn pages
  ([verbs page-list] (pages verbs page-list 0))
  ([verbs page-list initial]
    (oneofmany-render! = verbs (page-keys- page-list) initial)
    (partial oneofmany-update- pages verbs (page-keys- page-list) initial)))