(ns robot.core
  (:require [clojure.core.async :as async]
            [discljord.connections :as con]
            [discljord.messaging :as msg]
            [discljord.events :as dsc-events]
            [robot.events :as events]
            [com.brunobonacci.mulog :as u]
            [robot.components :as components]))

(u/start-publisher! {:type :console})

(defn -main
  [& _args]
  (let [channel (async/chan (async/buffer 100))
        conn-chan (con/connect-bot! (components/config :token) channel :intents #{})]
    (dsc-events/message-pump! channel events/event-handler)
    (msg/stop-connection! components/connection)
    (con/disconnect-bot! conn-chan)
    (async/close! channel)))
