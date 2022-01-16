(ns robot.commands
  (:require [slash.command :as cmd]
            [clojure.string :as str]
            [slash.command.structure :refer [command sub-command option]]
            [discljord.messaging :refer [bulk-overwrite-global-application-commands! create-interaction-response!]]
            [robot.components :refer [connection config]]))

(def all-commands
  (let [input-option (option "input" "Your input" :string :required true)]
    (command
     "reverse"
     "Reverses the input"
     :options
     [input-option])))

(cmd/defhandler reverse-command
  ["reverse"]
  _data
  [input]
  (let [{:keys [id token]} _data]
    (create-interaction-response! connection id token 4 :data {:content (str/reverse input)})))

(cmd/defpaths command-paths
  reverse-command)

(bulk-overwrite-global-application-commands! connection (:application-id config) [all-commands])
