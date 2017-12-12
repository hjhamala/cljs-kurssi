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
            [widgetshop.app.user]
            [widgetshop.app.products :as products]
            [widgetshop.app.ui :as ui]
            [widgetshop.category-page :as category-page]
            [widgetshop.product-page :as product-page]
            [widgetshop.cart-page :as cart-page]
            [widgetshop.checkout-page :as checkout-page]
            [widgetshop.success-page :as success-page]
            [widgetshop.error-page :as error-page]
            [goog.events :as events]
            [bidi.bidi :as bidi]
            [goog.history.EventType :as EventType])
  (:import [goog History]
           [goog.history EventType]))

(enable-console-print!)


(def keyword->page
  {:category-page category-page/get-page
   :product-page product-page/get-page
   :cart-page cart-page/get-page
   :success-page success-page/get-page
   :error-page error-page/get-page
   :checkout-page checkout-page/get-page})

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
    (ui/set-page! (:handler match))
    (ui/set-page! :category-page)))

(defonce history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (match-event! (.-token event))))
    (.setEnabled true)))

(defn show-page
  [app]
  (let [page (get keyword->page (ui/current-page app))]
    (page app)))

(defn info
  [app]
  (:logged-user app))

(rf/reg-sub
  :user-info
  (fn [db _]
    (info db)))

(defn widgetshop [app]
  [material-ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                {:palette {:text-color (color :green600)}})}
   [:div
    [material-ui/app-bar {:id "app-bar"
                          :title "Widgetshop!"
                 :icon-element-right
                 (r/as-element [material-ui/badge {:id "cart-size"
                                                   :badge-content @(rf/subscribe [:cart-size])
                                          :badge-style {:top 12 :right 12}}
                                [material-ui/icon-button {:on-click #(ui/switch-page! "cart")
                                                          :tooltip "Checkout"}
                                 (ic/action-shopping-cart)]])}]
    [:p "Hello user! " @(rf/subscribe [:user-info])]
    [material-ui/flat-button {:primary true :on-click #(rf/dispatch [:switch-user])} "Switch user"]
    [show-page app]]])

(defn main-component []
  [widgetshop @state/app])

(defn ^:export main []
  (rf/dispatch-sync [:initialize])
  (rf/dispatch [:load-product-categories])
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
