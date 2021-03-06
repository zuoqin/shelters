(defproject shelters "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89" :scope "provided"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [bk/ring-gzip "0.1.1"]
                 [ring.middleware.logger "0.5.0"]
                 [compojure "1.5.0"] 
                 [environ "1.0.3"]
                 [jarohen/chord "0.8.1"]
                 [prismatic/om-tools "0.4.0"]
                 [secretary "1.2.3"]   
              
                 [racehub/om-bootstrap "0.6.1"]
                 [org.omcljs/om "1.0.0-alpha41"]
                 [cljs-ajax "0.5.8"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [incanter "1.5.7"]
                 [net.unit8/tower-cljs "0.1.0"] ;;internalization technique

                 [clj-http "2.3.0"]
                 [org.clojure/data.json "0.2.6"] 

                 [cljsjs/chartjs "2.6.0-0"]
                ]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-environ "1.0.3"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "shelters.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main shelters.server
  :aot [shelters.server]


  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (run) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc"]

                :figwheel true
                ;; Alternatively, you can configure a function to run every time figwheel reloads.
                ;; :figwheel {:on-jsload "shelters.core/on-figwheel-reload"}

                :compiler {:main shelters.login
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/shelters.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :optimizations :none
                           :pretty-print true}}

               {:id "max"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main shelters.login
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled1/shelters.js"
                           :output-dir "resources/public/js/compiled1/out"
                           :source-map-timestamp false
                           :optimizations :none
                           :closure-warnings {:externs-validation :off}
                           :externs [
                             "cljsjs/common/bootstrap.ext.js"
                             "cljsjs/common/jquery-timepicker.ext.js"
                             "resources/public/javascript/bootstrap-datepicker.min.js"
                             "resources/public/javascript/bootstrap-treeview-1.2.0/dist/bootstrap-treeview.min.js"
                             "cljsjs/common/jquery.ext.js"
                             "resources/public/javascript/tinymce/tinymce.min.js"
                             "resources/public/javascript/externs.js"
                           ]
                           :pretty-print false}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main shelters.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main shelters.login
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/shelters.js"
                           :output-dir "resources/public/js/compiled2/out"
                           :source-map-timestamp false
                           :optimizations :simple
                           :closure-warnings {:externs-validation :off}
                           :externs [
                             "cljsjs/common/bootstrap.ext.js"
                             "cljsjs/common/jquery-timepicker.ext.js"
                             "resources/public/javascript/bootstrap-datepicker.min.js"
                             "resources/public/javascript/bootstrap-treeview-1.2.0/dist/bootstrap-treeview.min.js"
                             "cljsjs/common/jquery.ext.js"
                             "resources/public/javascript/tinymce/tinymce.min.js"
                             "resources/public/javascript/externs.js"
                           ]
                           :pretty-print false}}]}


  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Instead of booting a separate server on its own port, we embed
             ;; the server ring handler inside figwheel's http-kit server, so
             ;; assets and API endpoints can all be accessed on the same host
             ;; and port. If you prefer a separate server process then take this
             ;; out and start the server with `lein run`.
             :ring-handler user/http-handler

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}
)
