(ns zulip-focus.bot
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:import [java.io PushbackReader]))


(defn create-queue [config]
  (json/read-str (:body (client/post (:create_queue_url config)
                        {
                         :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                         :form-params { :event_types "[\"message\"]" }
                        }))
                 :key-fn keyword))

(defn ensure-queue [config]
  (try (client/get (:read_url config)
              {
                :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                :query-params { "queue_id" (:queue_id config) "last_event_id" -1 }
              })
         config
    (catch clojure.lang.ExceptionInfo e
      (let [params (create-queue config)]
        (assoc config :queue_id (:queue_id params) :last_event_id (:last_event_id params))))))

(defn load-config [filename]
  (with-open [r (io/reader filename)]
    (let [config (read (java.io.PushbackReader. r))]
      (ensure-queue config))))


(def config (load-config "config.clj"))

(defn get-all []
  (client/get (:read_url config)
              {
                :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                :query-params { "queue_id" (:queue_id config) "last_event_id" -1 }
              }))

(defn -main [& args]
  (println config))
  ;;(println (get-all)))
