(ns robot.components
  (:require [discljord.messaging :as msg]
            [cprop.core :as cpr]
            [clojure.core.cache :as cce]))

(def config (cpr/load-config :file "config.edn"))
(def connection (msg/start-connection! (config :token)))

(def cache-size 7200000)

(def sent-messages (ref (cce/ttl-cache-factory {} :ttl cache-size)))
(def deleted-messages (ref {}))
