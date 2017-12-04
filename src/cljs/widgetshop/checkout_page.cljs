(ns widgetshop.checkout-page
  (:require
    [reagent.core :as r]
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as material-ui]
    [cljs-react-material-ui.icons :as ic]
    [widgetshop.app.state :as state]
    [reagent.core :as reagent]
    [widgetshop.app.products :as products]
    [widgetshop.app.ui :as ui]
    [widgetshop.app.user :as user]
    [widgetshop.db :as db]
    [cljs.spec.alpha :as s]))

(defn t->v
  [e]
  (-> e .-target .-value))

(defn show-error
  [k m]
  (cond
    (nil? (k m)) false
    :else (not (s/valid? k (k m)))))

(defn valid-form?
  [m]
  (s/valid? ::db/checkout-form m))

(defn update-form
  [form k v]
  (swap! form (fn[m] (assoc m k v))))

(defn checkout-listing
  [app]
  (let [user-details (reagent/atom {})]
    (fn [app]
      [:div
       [material-ui/table
        [material-ui/table-body {:display-row-checkbox false}
          [material-ui/table-row
           [material-ui/table-row-column (if (show-error ::db/customer-name @user-details) "Name!!" "Name")]
           [material-ui/table-row-column [material-ui/text-field {:id (name ::db/customer-name)
                                                                  :onChange #(update-form user-details ::db/customer-name (t->v %))
                                                                  :value (or (::db/customer-name @user-details) "")}]]]
         [material-ui/table-row
          [material-ui/table-row-column (if (show-error ::db/address @user-details) "Address!!" "Address")]
          [material-ui/table-row-column [material-ui/text-field  {:id (name ::db/address)
                                                                  :onChange #(update-form user-details ::db/address (t->v %))
                                                                  :value (or (::db/address @user-details) "")}]]]
         [material-ui/table-row
          [material-ui/table-row-column (if (show-error ::db/visa-number @user-details) "Visa number!!" "Visa number")]
          [material-ui/table-row-column [material-ui/text-field  {:id (name ::db/visa-number)
                                                                  :onChange #(update-form user-details ::db/visa-number (t->v %))
                                                                  :value (or (::db/visa-number @user-details) "")}]]]]]
       [:div ""]
       (if (valid-form? @user-details)
         [material-ui/flat-button {:primary true :on-click #(user/order! app @user-details)} "Order"])])))

(defn get-page
  [app]
  [material-ui/paper
   [checkout-listing app]])
