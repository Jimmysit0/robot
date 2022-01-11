(ns robot.events
  (:require [clojure.tools.logging :as log]
            [discljord.formatting :as fmt]))

(defmulti event-handler (fn [type _data] type))

(defmethod event-handler :default [_ _])

(defmethod event-handler :ready
  [_ data]
  (log/info (str "Sucessfully connected as " (fmt/user-tag (:user data))
                 " (" (-> data :user :id) \))))
