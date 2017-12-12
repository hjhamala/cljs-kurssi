(ns widgetshop.product-page
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as material-ui]
    [cljs-react-material-ui.icons :as ic]
    [widgetshop.app.state :as state]
    [widgetshop.app.products :as products]
    [widgetshop.app.ui :as ui]))

(defn product-listing
  [app]
  (if-let [selected-product @(rf/subscribe [:selected-product])]
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
       [:div "Please review this fine piece of goodness"]
       [:div
        (let [review  @(rf/subscribe [:review-by-user product])]
         (for [review-button (range 1 6)]
           (if (= review review-button)
             [material-ui/raised-button {:key (str "button" review-button) :primary true :on-click #(rf/dispatch [:give-review product review-button])} review-button]
             [material-ui/flat-button {:key (str "button" review-button) :primary true :on-click #(rf/dispatch [:give-review product review-button])} review-button])))]
       [:div ""]
       [material-ui/flat-button {:primary true :on-click #(ui/switch-page! "category")} "Back to category selector"]])))

(defn get-page
  [app]
  [material-ui/paper
   [product-listing app]])