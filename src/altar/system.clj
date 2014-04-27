(ns altar.system
  (:require [clojure.set :refer [difference]])
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.tools.namespace.repl :refer [refresh]])
  (:require [taoensso.timbre :refer [debug info warn]])
  (:require [overtone.midi :refer [midi-in midi-out midi-handle-events]])
  (:require [altar.controls.page :refer [group]]))


(defn init-control-inputs! [inputs]
  (for [i inputs] (case (:type i)
    :midi (assoc i :port-in (midi-in (:port-name-in i)))
    (str "Unknown input" i))))


(defn init-control-outputs! [outputs]
  (for [i outputs] (case (:type i)
    :midi (let [port-out (midi-out (:port-name-out i))]
            (merge i {:port-out port-out
                      :verbs ((:verbs i) port-out)}))
    (str "Unknown output" i))))


(defn init-controllers! [controllers]
  (for [c controllers] (case (:type c)
    :midi (let [port-in  (midi-in (:port-name-in c))
                port-out (midi-out (:port-name-out c))
                verbs ((:verbs c) port-out)]
            (merge c {:port-in port-in  :port-out port-out  :verbs verbs}))
    (str "Unknown controller type " (:type c)))))


(defn state-updater [controls msg]
  (debug "MIDI receive:" (dissoc msg :msg :device :note :velocity))
  (debug "State is now:" (swap! controls (fn [c] ((:fn c) msg)))))


(defn get-output-verbs [controllers]
  (apply merge (for [c controllers] (:verbs c))))


(defn unknown-verb [verb]
  (fn [params]
    (warn "Unknown verb" verb params)))


(defn state-watcher [verbs]
  (fn [_ _ oldval newval]
    (let [old (:output oldval)  neu (:output newval)]
      (if (= old neu) nil
        (let [updated (difference neu old)]
          (debug "Updated" updated)
          (doseq [u updated]
            (let [verb (get u :verb :nop)  params (dissoc u :verb)
                  get-verb (fn [v] (get verbs v (unknown-verb v)))]
              ((get-verb verb) params))))))))


(defmacro defsystem [project-title & args]
 `(let [configuration#  (hash-map ~@args)
        conf#           (fn ([k#]    (get configuration# k# []))
                            ([k# n#] (get configuration# k# n#)))
        ctrls#          (conf# :controls)]
    (def  ~'system nil)

    (defn ~'init-  []  {:controllers (conf# :controllers)
                        :controls    ctrls#})
    (defn ~'init   []  (alter-var-root #'~'system (constantly (~'init-)))
                       (info "Initialized project \"" ~project-title "\":")
                       (pprint ~'system))

    (defn ~'start- [s#]
      (let [controllers#  (init-controllers! (:controllers s#))
            controls#     (atom (if (vector? ctrls#) (apply group ctrls#) ctrls#))
            brain#        (partial state-updater controls#)]
        (add-watch controls# :update (state-watcher (get-output-verbs controllers#)))
        (doseq [c# controllers#] (midi-handle-events (:port-in c#) brain#))
        {:controllers    controllers#
         :controls       controls#
         :brain          brain#}))
    (defn ~'start  [] (alter-var-root #'~'system ~'start-)
                      (info "Started project \"" ~project-title "\":")
                      (pprint ~'system))

    (defn ~'stop-  [s#]
      (doseq [i# (:controllers s#)] (.close (-> i# :port-in :transmitter)))
      {:controllers (conf# :controllers)})
    (defn ~'stop   [] (alter-var-root #'~'system ~'stop-)
                      (info "Stopped project \"" ~project-title "\":")
                      (pprint ~'system))

    (defn ~'reset  [] (if (nil? ~'system) nil (~'stop)) (refresh))
    (defn ~'go     [] (~'init) (~'start))))