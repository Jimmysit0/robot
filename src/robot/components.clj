(ns robot.components
  (:require [discljord.messaging :refer [start-connection!]]
            [cprop.core :refer [load-config]]))

(def config (load-config :file "config.edn"))
(def connection (start-connection! (config :token)))
