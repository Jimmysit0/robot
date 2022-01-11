(ns robot.core
  (:require [clojure.edn :as edn]
            [clojure.core.async :as async]
            [discljord.connections :refer [connect-bot! disconnect-bot!]]
            [discljord.messaging :as d.msg :refer [start-connection! stop-connection!]]
            [discljord.events :refer [message-pump!]]
            [robot.events :refer [event-handler]]))

(def config (edn/read-string (slurp "config.edn")))
(def connection (start-connection! (:token config)))

(defn -main
  [& _args]
  (let [channel (async/chan (async/buffer 100))
        conn-chan (connect-bot! (:token config) channel
                                :intents #{})]
    (message-pump! channel event-handler)
    (stop-connection! connection)
    (disconnect-bot! conn-chan)
    (async/close! channel)))
