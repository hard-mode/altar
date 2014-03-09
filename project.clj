(defproject altar "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories [["jogamp" "http://www.jogamp.org/deployment/maven/"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [overtone "0.9.1"]
                 [overtone/midi-clj "0.5.0"]
                 [org.clojars.originalsurfmex.processing/core "2.1"]
                 [org.jogamp.jogl/jogl-all-main "2.1.2-rc-20131025"]
                 [org.jogamp.gluegen/gluegen-rt-main "2.1.2-rc-20131025"]]

  :aot [quil.helpers.applet-listener quil.applet]
  :main ^:skip-aot altar.controls.button
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})