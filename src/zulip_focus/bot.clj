(ns zulip-focus.bot
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.java.io :as io])
  (:import [java.io PushbackReader]))

(defn load-config [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println( :zulip_api_key (load-config "config.clj")))
  ;(println(client/get "http://google.com"))
)
