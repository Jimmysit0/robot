(ns robot.commands
  (:require [slash.command :as cmd]
            [clojure.string :as str]
            [slash.command.structure :as stc]
            [discljord.messaging :as msg]
            [robot.components :as cmp]
            [discljord.cdn :as cdn]
            [discljord.formatting :as fmt]
            [slash.component.structure :as cmps]))

(def commands
  (let [input-option (stc/option "input"
                                    "Your input" :string :required true)
        user-option (stc/option "user"
                                   "The user" :user :required true)
        size-option (stc/option "size"
                                   "The desired size" :integer :required true
                                   :choices (map #(zipmap [:name :value] (repeat %)) (->> 16 (iterate #(* 2 %)) (take 9))))
        text-channel-option (stc/option "channel"
                                        "The desired channel" :channel :channel-types [:guild-text]
                                        :required true)]
    [(stc/command
     "reverse"
     "Reverses the input"
     :options
     [input-option])
     (stc/command
      "avatar"
      "Gets an user avatar"
      :options
      [user-option
       size-option])
     (stc/command
      "move"
      "Moves a conversation to the specified channel"
      :options
      [text-channel-option])
     (stc/command
      "snipe"
      "Sends the last deleted message in this server")]))

(cmd/defhandler reverse-command
  ["reverse"] 
  {:keys [id token]}
  [input]
  (msg/create-interaction-response!
   cmp/connection id token 4
   :data {:embeds [{:title "Reverse command"
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
    (msg/create-interaction-response!
     cmp/connection id token 4
     :data {:embeds [{:title (format "%s's avatar" username)
                      :image {:url avatar}
                      :color 0x4e87e6}]})))

(cmd/defhandler move-command
  ["move"]
  {:keys [id token channel-id guild-id] :as data}
  [channel]
  (if
      (= channel channel-id)
    (msg/create-interaction-response!
     cmp/connection id token 4
     :data {:embeds [{:description "Can't move the conversation to the same channel"
                      :color 0xFF0000}]})
      
    (let [message-id (get (deref (msg/create-message!                            
                                  cmp/connection channel
                                  :embed {:description (format "Continuing the conversation from %s"
                                                               (fmt/mention-channel channel-id))}))
                          :id)
          msg-url (str "https://discord.com/channels/" guild-id "/" channel "/" message-id)]
      (msg/create-interaction-response!
       cmp/connection id token 4
       :data {:embeds [{:description (str "Let's continue this conversation in "
                                          (fmt/mention-channel channel)
                                          " ("
                                          (fmt/embed-link "link" msg-url)
                                          ")")
                        :color 0x4e87e6}]
              :components [(cmps/action-row
                            (cmps/link-button
                             msg-url
                             :label "Link"))]})
      (let [slash-id (get (deref (msg/get-original-interaction-response!
                                  cmp/connection
                                  (cmp/config :application-id)
                                  (get data :token)))
                          :id)
            slash-url (str "https://discord.com/channels/" guild-id "/" channel-id "/" slash-id)]
        
        (msg/edit-message!
         cmp/connection channel message-id
         :embed {:description (str "Continuing the conversation from "
                                   (fmt/mention-channel channel-id)
                                   " ("
                                   (fmt/embed-link "link" msg-url)
                                   ")")
                 :color 0x4e87e6}
         :components [(cmps/action-row
                       (cmps/link-button
                        slash-url
                        :label "Link"))])))))

(cmd/defhandler snipe-command
  ["snipe"]
  {:keys [id token guild-id]}
  _
  (let [latest-deleted-message (get (deref cmp/deleted-messages) guild-id)]
    (if
        (contains? (deref cmp/deleted-messages) guild-id)
    (msg/create-interaction-response!
     cmp/connection id token 4
     :data {:embeds
            [{:description (format "Author: %s " (get latest-deleted-message :user))
              :color 0x4e87e6
              :fields [{:name (format "Message content: %s" (get latest-deleted-message :content))
                        :value "Gotcha!"
                        :inline true}]}]})
    (msg/create-interaction-response!
     cmp/connection id token 4
     :data {:embeds [{:description "There are no recent deleted messages in this server"
                      :color 0xFF0000}]}))))

(cmd/defpaths command-paths
  reverse-command
  avatar-command
  move-command
  snipe-command)

(msg/bulk-overwrite-global-application-commands! cmp/connection (cmp/config :application-id) commands)
