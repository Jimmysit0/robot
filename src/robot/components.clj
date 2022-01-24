(ns robot.components
  (:require [discljord.messaging :as msg]
            [cprop.core :as cprop]))

(def config (cprop/load-config :file "config.edn"))
(def connection (msg/start-connection! (config :token)))
