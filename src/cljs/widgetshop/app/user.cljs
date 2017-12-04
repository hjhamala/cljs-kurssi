(ns widgetshop.app.user
  (:require [js.uuid :as uuid]
            [widgetshop.app.state :as state]
            [widgetshop.app.ui :as ui]
            [widgetshop.server :as server]))

(defn info
  [app]
  (:logged-user app))

(defn switch
  [app]
  (assoc app :logged-user (uuid/genuuid)))

(defn order!
  [app user-details]
   (server/post! "/order" {:params {:user-details user-details
                                    :cart (:cart app)}
                          :on-success #(ui/switch-page! "success")
                          :on-failure #(do
                                         (println "error: " %)
                                         (ui/switch-page! "error"))}))

(defn switch!
  []
  (state/update-state!
    switch))