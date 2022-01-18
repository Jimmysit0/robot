(ns robot.core
  (:require [clojure.core.async :as async]
            [discljord.connections :refer [connect-bot! disconnect-bot!]]
            [discljord.messaging :refer [stop-connection!]]
            [discljord.events :refer [message-pump!]]
            [robot.events :refer [event-handler]]
            [com.brunobonacci.mulog :as u]
            [robot.components :refer [config connection]]))

(u/start-publisher! {:type :console})

(defn -main
  [& _args]
  (let [channel (async/chan (async/buffer 100))
        conn-chan (connect-bot! (config :token) channel :intents #{})]
    (message-pump! channel event-handler)
    (stop-connection! connection)
    (disconnect-bot! conn-chan)
    (async/close! channel)))
