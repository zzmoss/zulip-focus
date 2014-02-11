(ns zulip-focus.bot
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.java.io :as io])
  (:import [java.io PushbackReader]))

(defn load-config [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(def config (load-config "config.clj"))

(defn ensure-queue []
  (println (type (eval (:body (client/get (:read_url config)
              {
                :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                :query-params { "queue_id" (:queue_id config) "last_event_id" -1 }
              }))))))

(defn get-all []
  (client/get (:read_url config)
              {
                :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                :query-params { "queue_id" (:queue_id config) "last_event_id" -1 }
              }))

(defn -main [& args]
  (ensure-queue))
  ;;(println (get-all)))
