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
                :query-params { "queue_id" (:queue_id config) "last_event_id" -1 }
              })
         config
    (catch clojure.lang.ExceptionInfo e
      (let [params (create-queue config)]
        (assoc config :queue_id (:queue_id params) :last_event_id (:last_event_id params))))))

(defn load-config [filename]
    (let [config (read-string (slurp filename))
         updated-config (ensure-queue config)]
         (spit filename (with-out-str (pr updated-config)))
        updated-config))


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
