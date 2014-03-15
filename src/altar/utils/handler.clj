(ns altar.utils.handler)

; lazy list and coalesce implementation via
; http://stackoverflow.com/a/4087160/425219

(defn lazy-list*
  [& delayed-values]
  (when-let [delayed-values (seq delayed-values)]
    (reify
      clojure.lang.ISeq
      (first [this] @(first delayed-values))
      (next  [this] (lazy-list* (next delayed-values)))
      (more  [this] (or (next this) ())))))

(defmacro lazy-list
  [& values]
  `(lazy-list* ~@(map (fn [v] `(delay ~v)) values)))

(defn coalesce*
  [values]
  (first (keep identity values)))

(defmacro coalesce
  [& values]
  `(coalesce* (lazy-list ~@values)))


; Control message handlers

(defn get-handler [match & child-handlers]
  (println (str "initializing " match))
  (fn handler [msg]
    (when (= msg match)
      (println (str "captured by " match)))))

(defn handle [msg]
  ((get-handler nil
    (get-handler "a")
    (get-handler "b"
      (get-handler "c")
      (get-handler "d"
        (get-handler "e")))) "a"))