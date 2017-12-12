(ns widgetshop.app.user
  (:require [js.uuid :as uuid]
            [re-frame.core :as rf]
            [widgetshop.app.state :as state]
            [widgetshop.app.ui :as ui]
            [widgetshop.server :as server]))

(defn info
  [app]
  (:logged-user app))

(rf/reg-sub
  :user-info
  (fn [db _]
    (info db)))

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
(rf/reg-event-db
  :switch-user
  (fn [db _]
    (switch db)))
