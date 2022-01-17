(ns robot.commands
  (:require [slash.command :as cmd]
            [clojure.string :as str]
            [slash.command.structure :refer [command option]]
            [discljord.messaging :refer [bulk-overwrite-global-application-commands! create-interaction-response!]]
            [robot.components :refer [connection config]]
            [discljord.cdn :refer [resize effective-user-avatar]]))

(def all-commands
  (let [input-option (option "input" "Your input" :string :required true)
        user-option (option "user" "The users" :user :required true)
        size-option (option "size" "The desired size" :integer :required true :choices (map #(zipmap [:name :value] (repeat %)) [16 32 64 128 256 512 1024 2048 4096]))]
    (command
     "reverse"
     "Reverses the input"
     :options
     [input-option])
    (command
     "avatar"
     "Gets an user avatar"
     :options
     [user-option
      size-option])))

(cmd/defhandler reverse-command
  ["reverse"]
  {:keys [id token]}
  [input]
  (let [response (str "Your original input was: `" input "`, so your reversed input is: " "`"(str/reverse input)"`.")]
    (create-interaction-response! connection id token 4 :data {:content response})))

(cmd/defhandler avatar-command
  ["avatar"]
  {:keys [id token] :as data}
  [user size]
  (let [user-obj (get-in data [:data :resolved :users user])]
    (create-interaction-response! connection id token 4 :data {:content (resize (effective-user-avatar user-obj) size)})))

(cmd/defpaths command-paths
  reverse-command
  avatar-command)

(bulk-overwrite-global-application-commands! connection (:application-id config) [all-commands])
