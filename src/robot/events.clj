(ns robot.events
  (:require [com.brunobonacci.mulog :as u]         
            [slash.gateway :as gateway]
            [slash.core :as slash]
            [robot.commands :as cmds]))

(defmulti event-handler (fn [type _data] type
                          (u/log ::event :type type)
                          type))

(defmethod event-handler :default [_ _])

(def slash-handlers 
  (assoc gateway/gateway-defaults :application-command cmds/command-paths))

(defmethod event-handler :interaction-create 
  [_ data]
  ;; TODO: Use a single multimethod (defmulti) instead of a case, as all the command functions take the same arguments.
  (case (:name (:data data)) 
    "reverse" (cmds/reverse-command data)
    "avatar" (cmds/avatar-command data))
  (slash/route-interaction slash-handlers data))

(defmethod event-handler :ready
  [_ data]
  (u/log ::connected :id (:id (:user data))))
