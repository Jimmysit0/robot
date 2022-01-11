(ns robot.commands
  (:require [slash.command :as cmd]
            [slash.response :refer [channel-message]]
            [clojure.string :as str]))

(cmd/defhandler reverse-command
  ["reverse1"]
  _
  [input words]
  (channel-message
   {:content (if words
               (->> #"\s+" (str/split input) reverse (str/join " "))
               (str/reverse input))}))
      
(cmd/defpaths command-paths
  (cmd/group ["all"]
             reverse-command))
