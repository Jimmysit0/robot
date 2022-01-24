(ns robot.commands
  (:require [slash.command :as cmd]
            [clojure.string :as str]
            [slash.command.structure :as struct]
            [discljord.messaging :as msg]
            [robot.components :as components]
            [discljord.cdn :as cdn]))

(def commands
  (let [input-option (struct/option "input" "Your input" :string :required true)
        user-option (struct/option "user" "The user" :user :required true)
        size-option (struct/option "size" "The desired size" :integer :required true :choices (map #(zipmap [:name :value] (repeat %)) (->> 16 (iterate #(* 2 %)) (take 9))))]
    
    [(struct/command
     "reverse"
     "Reverses the input"
     :options
     [input-option])
     (struct/command
      "avatar"
      "Gets an user avatar"
      :options
      [user-option
       size-option])
     (struct/command
      "userinfo"
      "Gets an user information"
      :options
      [user-option])]))

(cmd/defhandler reverse-command
  ["reverse"] 
  {:keys [id token]}
  [input]
  (msg/create-interaction-response! components/connection id token 4 :data {:embeds [{:title "Reverse command"
                                                                                      :color 0x4e87e6
                                                                                      :fields [{:name "Input"
                                                                                                :value input
                                                                                                :inline true}
                                                                                               {:name "Reversed input"
                                                                                                :value (str/reverse input)
                                                                                                :inline true}]}]}))

(cmd/defhandler avatar-command
  ["avatar"]
  {:keys [id token] :as data}
  [user size]
  (let [user-obj (get-in data [:data :resolved :users user])
        username (get-in data [:data :resolved :users user :username])
        avatar (cdn/resize (cdn/effective-user-avatar user-obj) size)]
    (msg/create-interaction-response! components/connection id token 4 :data {:embeds [{:title (format "%s's avatar" username)
                                                                                        :image {:url avatar}
                                                                                        :color 0x4e87e6}]})))

(cmd/defpaths command-paths
  reverse-command
  avatar-command)

(msg/bulk-overwrite-guild-application-commands! components/connection (components/config :application-id) (components/config :guild-id) commands)
