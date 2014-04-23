(ns altar.controls.page
  (:require [altar.controls.button :refer [oneofmany oneofmany-update- oneofmany-render!]])
  (:require [altar.utils.midi :refer [midi-match midi-cmp]]))


; (defn page-keys-
;   [page-list]
;   (apply sorted-set-by midi-cmp (take-nth 2 page-list)))


; (defn indexed-page-keys-
;   [page-list]
;   (map-indexed vector (apply sorted-set-by midi-cmp (take-nth 2 page-list))))


; (defn page-index-
;   [mask page-keys]
;   (first (first (filter #(= (second %) mask) page-keys))))


(defn pages-update-
  "A variant of oneofmany-update- which preserves the full page-list. "
  [f verbs page-list initial-state msg]
    (let [matched (first (filter (complement nil?)
            (for [p (map-indexed vector (take-nth 2 page-list))]
              (if (midi-match (assoc (second p) :command :note-on) msg)
                (first p) nil))))
          next-state (if (nil? matched) initial-state matched)]
      (f verbs page-list next-state)))


(defn pages
  ([verbs page-list] (pages verbs page-list 0))
  ([verbs page-list initial-state]
    (oneofmany-render! = verbs (take-nth 2 page-list) initial-state)
    (partial pages-update- pages verbs page-list initial-state)))