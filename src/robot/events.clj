(ns robot.events
  (:require [com.brunobonacci.mulog :as u]         
            [slash.core :as slash]
            [robot.commands :as cmds]
            [slash.gateway :as gtw]
            [clojure.core.cache :as cce]
            [robot.components :as cmp]))

(defmulti event-handler (fn [type _data] type
                          (u/log ::event :type type)
                          type))

(defmethod event-handler :default [_ _])

(defmethod event-handler :message-create
  [_ {{bot :bot author :username} :author :keys [content id guild-id]}]
  (when-not bot
    (dosync (alter cmp/sent-messages cce/miss [guild-id id] {:content content :user author}))))

(defmethod event-handler :message-delete
  [_ {:keys [guild-id id]}]
  (let [keys [guild-id id]]
    (dosync
     (alter cmp/deleted-messages update guild-id #(or (cce/lookup @cmp/sent-messages keys) %))
     (commute cmp/sent-messages cce/evict keys))))
         
(defmethod event-handler :interaction-create 
  [_ data]
  (let [slash-handlers (assoc gtw/gateway-defaults :application-command cmds/command-paths)]
    (slash/route-interaction slash-handlers data)))

(defmethod event-handler :ready
  [_ data]
  (u/log ::connected :id (:id (:user data))))
