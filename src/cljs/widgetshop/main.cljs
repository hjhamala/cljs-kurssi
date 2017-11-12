(ns widgetshop.main
  "Main entrypoint for the widgetshop frontend."
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as material-ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.app.products :as products]
            [widgetshop.app.ui :as ui]
            [widgetshop.category-page :as category-page]
            [widgetshop.product-page :as product-page]))

(def keyword->page
  {:category-page category-page/get
   :product-page product-page/get })

(defn show-page
  [app]
  (let [page (get keyword->page (ui/current-page app))]
    (page app)))

(defn widgetshop [app]
  [material-ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                {:palette {:text-color (color :green600)}})}
   [:div
    [material-ui/app-bar {:id "app-bar"
                          :title "Widgetshop!"
                 :icon-element-right
                 (r/as-element [material-ui/badge {:id "cart-size"
                                                   :badge-content (products/cart-size app)
                                          :badge-style {:top 12 :right 12}}
                                [material-ui/icon-button {:tooltip "Checkout"}
                                 (ic/action-shopping-cart)]])}]
    [show-page app]]])

(defn main-component []
  [widgetshop @state/app])

(defn ^:export main []
  (products/load-product-categories!)
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
