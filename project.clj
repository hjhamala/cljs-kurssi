(defproject cljs-kurssi "0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha19"]
                 [org.clojure/clojurescript "1.9.660"]

                 ;; Component library
                 [com.stuartsierra/component "0.3.2"]

                 ;; Logging library
                 [com.taoensso/timbre "4.10.0"]

                 ;; PostgreSQL JDBC driver and connection pooling
                 [org.postgresql/postgresql "42.1.4"]
                 [com.zaxxer/HikariCP "2.6.1"]
                 [org.clojure/java.jdbc "0.7.1"]

                 ;; embedded postgres
                 [com.opentable.components/otj-pg-embedded "0.7.1"]

                 ;; http-kit HTTP server (and client)
                 [http-kit "2.2.0"]

                 ;; Routing library for publishing services
                 [compojure "1.6.0"]

                 ;; Transit data format libraries for backend and frontend
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.239"]

                 ;; Ajax library for frontend
                 [cljs-ajax "0.7.2"]

                 ;; Doo for testing
                 [lein-doo "0.1.7"]

                 ;; Routing library for the frontend
                 [bidi "2.1.2"]

                 ;; Frontend UI-libraries
                 [reagent "0.7.0"]
                 [cljsjs/react "15.6.1-1"]
                 [cljsjs/react-dom "15.6.1-1"]
                 [cljs-react-material-ui "0.2.48"]
                 [figwheel "0.5.11"]

                 ;; Something pulls an old guava which prevents closure compiler
                 ;; override here
                 [com.google.guava/guava "21.0"]

                 ;;
                 [re-frame "0.10.2"]
                 [day8.re-frame/http-fx "0.1.4"]
                 ]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-cooper "1.2.2"]
            [lein-figwheel "0.5.11"]
            [lein-doo "0.1.7"]]

  :repl-options {:port 51902
                 :init-ns widgetshop.main
                 :init (-main)
                 :resource-paths ^:replace ["resources" "dev/resources" "target/figwheel"]}

  ;; Sources for backend: clj and cljc (shared with frontend)
  :source-paths ["src/clj" "src/cljc"]

  ;; Configure ClojureScript builds
  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs" "src/cljc"]
                :figwheel     {:on-jsload "widgetshop.main/reload-hook"}
                :compiler     {:optimizations :none
                               :foreign-libs [{:file "src" :module-type :es6}]
                               :npm-deps {:average "0.1.0"}
                               :source-map    true
                               :output-to     "resources/public/js/widgetshop.js"
                               :output-dir    "resources/public/js/out"}}]}

  ;; Add doo-runner
  :profiles
  {:doo {
         :cljsbuild
         {:builds
          [{:id           "cljs-test"
            :source-paths ["src/cljs" "src/cljc" "test/cljs/"]
            :compiler     {:output-to     "out/browser_tests.js"
                           :foreign-libs [{:file "src" :module-type :es6}]
                           :npm-deps {:average "0.1.0"}
                           :main          "widgetshop.test-runner"
                           :target        :phantom
                           :optimizations :none}}

           ]}}}


  :aliases {
            "run-doo"      ["with-profile" "doo" "doo" "phantom" "cljs-test"]
            }

  :cooper {"cljsbuild" ["lein" "trampoline" "cljsbuild" "auto"]
           "backend" ["lein" "trampoline" "repl" ":headless"]
           "doo" ["lein" "trampoline" "run-doo"]
           "figwheel" ["lein" "trampoline" "figwheel"]}

  :main widgetshop.main)
