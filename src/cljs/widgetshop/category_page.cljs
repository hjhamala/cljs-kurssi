(ns widgetshop.category-page
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

(defn category-selector
  []
  ;; Product category selection
    [material-ui/select-field {:id "select-product-category"
                               :floating-label-text "Select product category"
                               :value (:id  @(rf/subscribe [:category]))
                               :on-change (fn [evt idx value]
                                            (rf/dispatch [:select-category value]))}
     (for [{:keys [id name] :as category} @(rf/subscribe [:categories])]
       ^{:key id}
       [material-ui/menu-item {:id (str "menu-item-id-" id)
                               :value id :primary-text name}])])

(defn products-listing
  []
  ;; Product listing for the selected category
  (let [products @(rf/subscribe [:products-from-selected-category])]
    (if (= :loading products)
      [material-ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

      [material-ui/table
       [material-ui/table-header {:display-select-all false :adjust-for-checkbox false}
        [material-ui/table-row
         [material-ui/table-header-column "Name"]
         [material-ui/table-header-column "Description"]
         [material-ui/table-header-column "Price (€)"]
         [material-ui/table-header-column "Review"]
         [material-ui/table-header-column "Add to cart"]]]
       [material-ui/table-body {:display-row-checkbox false}
        (doall (for [{:keys [id name description price] :as product} products]
          ^{:key id}
          [material-ui/table-row
           [material-ui/table-row-column [:div {:on-click #(rf/dispatch [:select-product product])} name]]
           [material-ui/table-row-column description]
           [material-ui/table-row-column price]
           [material-ui/table-row-column @(rf/subscribe [:review product])]
           [material-ui/table-row-column
            [material-ui/flat-button {:id (str "add-to-cart-button-" id)
                                      :primary true :on-click #(rf/dispatch [:add-to-cart product])}
             "Add to cart"]]]))]])))

(defn get-page
  [app]
  [material-ui/paper
   (when-not @(rf/subscribe [:loading-categories?])
     [category-selector])
   [products-listing]
   [material-ui/raised-button {:label        "Click me"
                               :icon         (ic/social-group)
                               :on-click     #(println "clicked")}]])