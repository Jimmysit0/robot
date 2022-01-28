(ns robot.components
  (:require [discljord.messaging :as msg]
            [cprop.core :as cpr]))

(def config (cpr/load-config :file "config.edn"))
(def connection (msg/start-connection! (config :token)))
