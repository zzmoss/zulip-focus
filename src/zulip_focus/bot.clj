(ns zulip-focus.bot
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.json :as json])
  )


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
                :query-params { "queue_id" (:queue_id config) "last_event_id" (:last_event_id config) }
              })
         config
    (catch clojure.lang.ExceptionInfo e
      (let [params (create-queue config)]
        (assoc config :queue_id (:queue_id params) :last_event_id (str (:last_event_id params)))))))

(defn load-config [filename]
    (let [config (read-string (slurp filename))
         updated-config (ensure-queue config)]
         (spit filename (with-out-str (pr updated-config)))
        updated-config))


(def config (load-config "config.clj"))

(defn parse-events [events]
  (map (fn [event] (:content (:message event)))
       events))

(defn get-once []
  (let [message (client/get (:read_url config)
              {
                :basic-auth [(:zulip_bot_email config) (:zulip_api_key config)]
                :query-params { "queue_id" (:queue_id config) "last_event_id" (:last_event_id config) }
              })
        body (json/read-str (:body message) :key-fn keyword)
        event-type (:type (first (:events body)))]
    (if (not (= event-type "heartbeat"))

      (parse-events(:events body))
     )))

(defn get-forever []
    (let [message (get-once)]
      (if message (println message))
      (recur)))


(defn -main [& args]
  (println "Starting...")
  (get-forever))
