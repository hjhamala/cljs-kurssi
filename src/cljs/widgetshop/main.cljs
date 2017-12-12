(ns widgetshop.main
  "Main entrypoint for the widgetshop frontend."
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [re-frame.core :as rf]
            [js.uuid]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as material-ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.app.user :as user]
            [widgetshop.common-page :as common-page]
            [widgetshop.app.ui :as ui]
            [goog.events :as events]
            [bidi.bidi :as bidi]
            [goog.history.EventType :as EventType])
  (:import [goog History]
           [goog.history EventType]))

(enable-console-print!)

(def ui-routes
  ["/" {"category" :category-page
        "product" :product-page
        "cart" :cart-page
        "success" :success-page
        "error" :error-page
        "checkout" :checkout-page}])

(defn match-event!
  [x]
  (if-let [match (bidi/match-route ui-routes x)]
    (rf/dispatch [:set-page (:handler match)])
    (rf/dispatch [:set-page  :category-page])))

(defonce history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (match-event! (.-token event))))
    (.setEnabled true)))

;; These are needed for weird compilation problem



(defn ^:export main []
  (rf/dispatch-sync [:initialize])
  (rf/dispatch [:load-product-categories])
  (r/render (fn []  [common-page/main-component]) (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
