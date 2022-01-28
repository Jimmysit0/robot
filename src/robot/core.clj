(ns robot.core
  (:require [clojure.core.async :as a]
            [discljord.connections :as con]
            [discljord.messaging :as msg]
            [discljord.events :as dsc-evts]
            [robot.events :as evts]
            [com.brunobonacci.mulog :as u]
            [robot.components :as components]))

(u/start-publisher! {:type :console})

(defn -main
  [& _args]
  (let [channel (a/chan (a/buffer 100))
        conn-chan (con/connect-bot! (components/config :token) channel :intents #{})]
    (dsc-evts/message-pump! channel evts/event-handler)
    (msg/stop-connection! components/connection)
    (con/disconnect-bot! conn-chan)
    (a/close! channel)))
