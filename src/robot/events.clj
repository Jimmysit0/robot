(ns robot.events
  (:require [com.brunobonacci.mulog :as u]         
            [slash.gateway :as gtw]
            [slash.core :as slash]
            [robot.commands :as cmds]))

(defmulti event-handler (fn [type _data] type
                          (u/log ::event :type type)
                          type))

(defmethod event-handler :default [_ _])

(defmethod event-handler :interaction-create 
  [_ data]
  (let [slash-handlers (assoc gtw/gateway-defaults :application-command cmds/command-paths)]
    (slash/route-interaction slash-handlers data)))

(defmethod event-handler :ready
  [_ data]
  (u/log ::connected :id (:id (:user data))))
