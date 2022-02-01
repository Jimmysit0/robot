(ns robot.events
  (:require [com.brunobonacci.mulog :as u]         
            [slash.core :as slash]
            [robot.commands :as cmds]
            [slash.gateway :as gtw]
            [clojure.core.cache :as cce]))

(defmulti event-handler (fn [type _data] type
                          (u/log ::event :type type)
                          type))

(defmethod event-handler :default [_ _])

(defmethod event-handler :interaction-create 
  [_ data]
  (let [slash-handlers (assoc gtw/gateway-defaults :application-command cmds/command-paths)]
    (slash/route-interaction slash-handlers data)))

(def message-cache (atom (cce/ttl-cache-factory {} :ttl 7200000)))

(defmethod event-handler :message-create
  [_ {{bot :bot} :author :keys [content id]}]
  (when-not bot
    (swap! message-cache assoc id content)
    (println (deref message-cache))))

(def deleted-message (atom 0))

(defmethod event-handler :message-delete
  [_ {:keys [id]}]
  (let [content (get (deref message-cache) id)]
    (when
        (contains? (deref message-cache) id)
      (swap! deleted-message :message content))))

(defmethod event-handler :ready
  [_ data]
  (u/log ::connected :id (:id (:user data))))
