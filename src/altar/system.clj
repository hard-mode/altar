(ns altar.system
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [overtone.midi :refer [midi-in midi-out midi-handle-events]])
  (:require [altar.controls.page :refer [group]]))


(defn init-control-inputs- [inputs]
  (let [input-pairs (map vector (take-nth 2 inputs)
                                (take-nth 2 (rest inputs)))]
    (for [i input-pairs]
      (case (first i)
        :midi (midi-in (second i))
        (str "Unknown input" i)))))


(defn init-control-outputs- [outputs]
  (let [output-pairs (map vector (take-nth 2 outputs)
                                 (take-nth 2 (rest outputs)))]
    (for [i output-pairs]
      (case (first i)
        :midi (midi-out (second i))
        (str "Unknown output" i)))))


(defmacro defsystem [project-title & args]
 `(let [configuration#  (hash-map ~@args)
        conf#           (fn ([k#]    (get configuration# k# []))
                            ([k# n#] (get configuration# k# n#)))]
    (def  ~'system nil)

    (defn ~'init-  []  {:controllers (conf# :controllers)})
    (defn ~'init   []  (alter-var-root #'~'system (constantly (~'init-)))
                       (println (str "\nInitialized project \"" ~project-title "\":"))
                       (pprint ~'system))

    (defn ~'start- [s#]
      (let [ctrls#    (conf# :controls)
            controls# (atom (if (vector? ctrls#) (apply group ctrls#) ctrls#))
            ins#      (init-control-inputs- (:controllers s#))
            brain#    (fn [msg#] (println "\nMSG=>" (dissoc msg# :msg :device))
                                 (println "NEWCTRL=>" (swap! controls# (fn [c#] ((:fn c#) msg#)))))]
        {:ctrl-ins  ins#
         :ctrl-outs (init-control-outputs- (:controllers s#))
         :controls  controls#
         :brain     brain#
         :receivers (for [i# ins#] (midi-handle-events i# brain#))}))
    (defn ~'start  [] (alter-var-root #'~'system ~'start-)
                      (println (str "\nStarted project \"" ~project-title "\":"))
                      (pprint ~'system))

    (defn ~'stop-  [s#]
      (doseq [i# (:ctrl-ins s#)] (println i#) (.close (:transmitter i#)))
      {:controllers (conf# :controllers)})
    (defn ~'stop   [] (alter-var-root #'~'system ~'stop-)
                      (println (str "\nStopped project \"" ~project-title "\":"))
                      (pprint ~'system))

    (defn ~'reset  [] (if (nil? ~'system) nil (~'stop)) (refresh))
    (defn ~'go     [] (~'init) (~'start))))