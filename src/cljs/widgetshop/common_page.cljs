(ns widgetshop.common-page
  (:require [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as material-ui]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [widgetshop.app.ui :as ui]
            [widgetshop.app.user]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.checkout-page :as checkout-page]
            [widgetshop.error-page :as error-page]
            [widgetshop.category-page :as category-page]
            [widgetshop.product-page :as product-page]
            [widgetshop.cart-page :as cart-page]
            [widgetshop.success-page :as success-page]))

(def keyword->page
  {:category-page category-page/get-page
   :product-page  product-page/get-page
   :cart-page     cart-page/get-page
   :success-page  success-page/get-page
   :error-page    error-page/get-page
   :checkout-page checkout-page/get-page})

(defn show-page
  [app]
  (let [page (get keyword->page @(rf/subscribe [:current-page]))]
    (page app)))

(defn widgetshop [app]
  [material-ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                 {:palette {:text-color (color :green600)}})}
   [:div
    [material-ui/app-bar {:id    "app-bar"
                          :title "Widgetshop!"
                          :icon-element-right
                                 (r/as-element [material-ui/badge {:id            "cart-size"
                                                                   :badge-content @(rf/subscribe [:cart-size])
                                                                   :badge-style   {:top 12 :right 12}}
                                                [material-ui/icon-button {:on-click #(ui/switch-page! "cart")
                                                                          :tooltip  "Checkout"}
                                                 (ic/action-shopping-cart)]])}]
    [:p "Hello user! " @(rf/subscribe [:user-info])]
    [material-ui/flat-button {:primary true :on-click #(rf/dispatch [:switch-user])} "Switch user"]
    [show-page app]]])

(defn main-component []
      [widgetshop @state/app])