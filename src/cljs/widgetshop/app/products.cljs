(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :as state]
            [widgetshop.server :as server]))

(defn- products-by-category [app category products]
  (assoc-in app [:products-by-category category] products))

(defn- set-categories [app categories]
  (assoc-in app [:categories] categories))

(defn- load-products-by-category! [{:keys [categories] :as app} server-get-fn! category-id]
  (let [category (some #(when (= (:id %) category-id) %) categories)]
    (server-get-fn! category)
    (-> app
        (assoc-in [:ui :selected-category] category)
        (assoc :category category)
        (assoc-in [:products-by-category category] :loading))))

(defn select-category-by-id! [category-id]
  (state/update-state!
    load-products-by-category!
    (fn [category]
      (server/get! (str "/products/" (:id category))
                   {:on-success #(state/update-state! products-by-category category %)}))
    category-id))

(defn load-product-categories! []
  (server/get! "/categories" {:on-success #(state/update-state! set-categories %)}))

(defn by-category
  [app selected-category]
  ((:products-by-category app) selected-category))

(defn inc-if-nil-1
  "If value is nil returns 1 otherwise increases"
  [v]
  (if (nil? v)
    1
    (inc v)))

(defn add-to-cart
  [app category]
  (update-in app [:cart category] inc-if-nil-1))

(defn add-to-cart!
  [product]
  (state/update-state!
    add-to-cart
    product))

(defn cart-size
  [app]
  (reduce (fn [val [product size]] (+ val size)) 0 (:cart app)))


