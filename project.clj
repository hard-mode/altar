(defproject

  altar
    "0.1.0-SNAPSHOT"

  :description
    "FIXME: write description"

  :url
    "http://example.com/FIXME"

  :license {
    :name "Eclipse Public License"
    :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [com.taoensso/timbre "3.1.6"]
    [overtone            "0.9.1"]
    [overtone/midi-clj   "0.5.0"]]

  :git-dependencies [
    ["https://github.com/jaudiolibs/jnajack.git"]]

  :profiles {
    :dev {
      :source-paths ["dev"]
      :dependencies [
        [org.clojure/tools.namespace "0.2.4"]
        [clj-ns-browser              "1.3.1"]]}
    :uberjar { :aot :all }}

  :plugins [[lein-git-deps     "0.0.1-SNAPSHOT"]
            [cider/cider-nrepl "0.8.2"]
            [mvxcvi/whidbey    "1.0.0"]]

  :repl-options {
    :prompt  (fn [ns] (str ns " ;) " ))
    :init-ns altar.scratch})
