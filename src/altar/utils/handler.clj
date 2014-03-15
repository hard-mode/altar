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
  `(lazy-list* ~@(map (fn [v] `(delay ~v)) values))

(defn coalesce*
  [values]
  (first (keep identity values)))

(defmacro coalesce
  [& values]
  `(coalesce* (lazy-list ~@values)))


; Control message handlers

(defn get-handler [match & child-handlers]
  (println (str "initializing " formsg))
  (fn handler [msg]
    (when (= msg formsg)
      (println (str "captured by " formsg)))))

(get-handler nil
  (get-handler "a")
  (get-handler "b"
    (get-handler "c")
    (get-handler "d"
      (get-handler "e"))))