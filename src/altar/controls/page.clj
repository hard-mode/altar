(ns altar.controls.page
  (:require [clojure.pprint :refer [pprint]])
  #_(:require [altar.controls.button :refer [oneofmany oneofmany-update- oneofmany-render!]])
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


(defn group- [state output members]
  {:fn (fn [msg]
    (let [next-members (doall (for [m members] ((:fn m) msg)))
          next-state (concat (for [n next-members] (:state n)))
          next-output (set (concat (for [n next-members] (:output n))))]
      (group- next-state next-output next-members)))
   :output output
   :state state})


(defn group
  "Handles a number of controls, in order. "
  [& members] (group- [] [] members))


; (defn pages-update-
;   "A variant of oneofmany-update- which preserves the full page-list. "
;   [f verbs page-list initial-state msg]
;   (let [next-state (or
;          (first (filter (complement nil?)
;            (for [p (map-indexed vector (take-nth 2 page-list))]
;              (if (midi-match (assoc (second p) :command :note-on) msg)
;                (first p) nil))))
;          initial-state)

;         page-keys (take-nth 2 page-list)
;         page-contents (take-nth 2 (rest page-list))
;         next-page (nth page-contents next-state)
;         updated-page ((next-page verbs) msg)
;         updated-pages (assoc (vec page-contents) next-state updated-page)
;         next-page-list (apply concat (map vector page-keys updated-pages))]
;         (println)
;         (println "next page" next-page)
;         (println "updated page" updated-page)
;         (println "updated pages" updated-pages)
;         (println "old pages" page-list)
;         (println "new pages" next-page-list)
;     (f verbs page-list next-state)))


; (defn pages
;   ([verbs page-list] (pages verbs page-list 0))
;   ([verbs page-list initial-state]
;     (oneofmany-render! = verbs (take-nth 2 page-list) initial-state)
;     (partial pages-update- pages verbs page-list initial-state)))