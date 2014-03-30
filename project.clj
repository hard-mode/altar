(defproject altar "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [overtone "0.9.1"]
                 [overtone/midi-clj "0.5.0"]]

  :repl-options {:init-ns altar.scratch}

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})