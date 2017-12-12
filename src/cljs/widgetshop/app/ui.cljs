(ns widgetshop.app.ui
  (:require [widgetshop.app.state :as state]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]))

(defn switch-page!
  [page]
  (set! js/window.location.href (str "#/" page)))

(defn current-page
  [app]
  (-> (:ui app) :page))

(defn set-page
  [app page-kw]
  (assoc-in app [:ui :page] page-kw))

(defn set-page!
  [page-kw]
  (state/update-state!
    set-page
    page-kw))

(defn selected-product
  [app]
  (-> (:ui app) :selected-product))

(rf/reg-sub
  :selected-product
  (fn [db _]
    (selected-product db)))

(defn select-product
  [db category]
  (-> (assoc-in db [:ui :selected-product] category)
      (assoc-in [:ui :page] :product-page)))

(rf/reg-event-db
  :select-product
  [state/check-spec-interceptor]
  (fn [db [_ product]]
    (println "select product")
    (switch-page! "product")
    (select-product db product)))

