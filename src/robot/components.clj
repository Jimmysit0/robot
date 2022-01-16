(ns robot.components
  (:require [clojure.edn :as edn]
            [discljord.messaging :refer [start-connection!]]))

(def config (edn/read-string (slurp "config.edn")))
(def connection (start-connection! (:token config)))
