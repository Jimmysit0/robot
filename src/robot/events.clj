(ns robot.events
  (:require [com.brunobonacci.mulog :as u]         
            [slash.gateway :refer [gateway-defaults]]
            [slash.core :as slash]
            [robot.commands :refer [reverse-command command-paths]]))

(defmulti event-handler (fn [type _data] type
                          (u/log ::event :type type)
                          type))

(defmethod event-handler :default [_ _])

(def slash-handlers 
  (assoc gateway-defaults :application-command command-paths))

(defmethod event-handler :interaction-create 
  [_ data]
  (case (:name (:data data))
    "reverse" (reverse-command data))
  (slash/route-interaction slash-handlers data))

(defmethod event-handler :ready
  [_ data]
  (u/log ::connected :id (:id (:user data))))
