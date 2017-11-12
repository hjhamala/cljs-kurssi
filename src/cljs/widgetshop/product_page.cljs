(ns widgetshop.product-page
  (:require
    [reagent.core :as r]
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as material-ui]
    [cljs-react-material-ui.icons :as ic]
    [widgetshop.app.state :as state]
    [widgetshop.app.products :as products]
    [widgetshop.app.ui :as ui]))


(defn product-listing
  [app]
  (if-let [selected-product (ui/selected-product app)]
    (let [{:keys [id name description price] :as product} selected-product]
      [:div
       [:h1 "Product details"]
      [material-ui/table
       [material-ui/table-body {:display-row-checkbox false}
        [material-ui/table-row
         [material-ui/table-row-column "Name"]
         [material-ui/table-row-column name]]
        [material-ui/table-row
         [material-ui/table-row-column "Description"]
         [material-ui/table-row-column description]]
        [material-ui/table-row
         [material-ui/table-row-column "Price"]
         [material-ui/table-row-column price]]]]
       [material-ui/flat-button {:primary true :on-click #(ui/set-page! :category-page)} "Back to category selector"]])))

(defn get
  [app]
  [material-ui/paper
   [product-listing app]])