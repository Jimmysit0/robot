(ns robot.handler
  (:require [slash.command.structure :refer [command sub-command option]]
            [discljord.messaging :refer [bulk-overwrite-guild-application-commands! bulk-overwrite-global-application-commands!]]
            [robot.core :refer [connection config]]))

(def all-commands
  (let [input-option (option "input" "Your input" :string :required true)]
  (command
   "all"
   "All commands"
   :options
   [(sub-command
     "reverse1"
     "Reverses the input"
     :options
     [input-option
      (option "words" "Reverse words instead of characters?" :boolean)])])))

(println (deref (bulk-overwrite-global-application-commands! connection (:application-id config) [all-commands])))
