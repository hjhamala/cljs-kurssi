(ns widgetshop.app.user
  (:require [js.uuid :as uuid]
            [widgetshop.app.state :as state]))

(defn info
  [app]
  (:logged-user app))

(defn switch
  [app]
  (assoc app :logged-user (uuid/genuuid)))

(defn switch!
  []
  (state/update-state!
    switch))