(ns widgetshop.cart-page
  (:require
    [reagent.core :as r]
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as material-ui]
    [cljs-react-material-ui.icons :as ic]
    [widgetshop.app.state :as state]
    [reagent.core :as reagent]
    [widgetshop.app.products :as products]
    [widgetshop.app.ui :as ui]))

(defn t->v
  [e]
  (-> e .-target .-value))

(defn update-cart!
  [cart product amount]
  (swap! cart (fn[v] (assoc v product amount))))

(defn update-amount
  [editable-cart product e]
  (update-cart! editable-cart product (if (clojure.string/blank? (t->v e))
                                        ""
                                        (js/parseInt (t->v e)))))

(defn cart-listing
  [app]
  (let [editable-cart (reagent/atom (products/cart app))]
    (fn [app]
      (let [cart @editable-cart
            cart-edited? (not= cart (products/cart app))]
        [:div
          [material-ui/table
           [material-ui/table-header {:display-select-all false :adjust-for-checkbox false}
            [material-ui/table-row
             [material-ui/table-header-column "Name"]
             [material-ui/table-header-column "Description"]
             [material-ui/table-header-column "Price (€)"]
             [material-ui/table-header-column "Amount"]]]
           [material-ui/table-body {:display-row-checkbox false}
            (for [[{:keys [id name description price] :as product} amount] cart]
              [material-ui/table-row {:key id}
               [material-ui/table-row-column name]
               [material-ui/table-row-column description]
               [material-ui/table-row-column price]
               [material-ui/table-row-column [material-ui/text-field
                                              {:id (str "text-field-" id)
                                               :onChange #(update-amount editable-cart product %)
                                               :value amount}]]])]]
        [:div ""]
        (if cart-edited?
          [material-ui/flat-button {:primary true :on-click #(do (products/update-cart! cart)(ui/switch-page! "category"))} "Save edits"]
          [material-ui/flat-button {:primary true :on-click #(ui/switch-page! "category")} "Back to category selector"])
         [:div ""]
         [material-ui/flat-button {:primary true :on-click #(do (products/update-cart! cart)(ui/switch-page! "checkout"))} "Checkout and order"]]))))

(defn get-page
  [app]
  [material-ui/paper
   [cart-listing app]])
