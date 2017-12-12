(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :as state]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [average]
            [widgetshop.server :as server]
            [ajax.core :as ajax]))

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


(rf/reg-event-db
  :load-product-success
  [state/check-spec-interceptor]
  (fn [db [_ result]]
    (println "success")
    (assoc-in db [:categories] result)))

(rf/reg-event-db
  :load-product-failure
  [state/check-spec-interceptor]
  (fn [db result]
    (println result)
    db))

(rf/reg-event-fx
  :load-product-categories
  [state/check-spec-interceptor]
  (fn [{db :db} _]
    (println "got call")
    {:http-xhrio {:method          :get
                  :uri             "/categories"
                  :format          (ajax/transit-request-format)
                  :response-format (ajax/transit-response-format)
                  :on-success      [:load-product-success]
                  :on-failure      [:load-product-failure]}
     :db         db}))



(rf/reg-event-db
  :select-category-success
  [state/check-spec-interceptor]
  (fn [db [_ category result]]
    (assoc-in db [:products-by-category category] result)))

(rf/reg-event-fx
  :select-category
  [state/check-spec-interceptor]
  (fn [{db :db} [_ category-id]]
    (let [category (some #(when (= (:id %) category-id) %) (:categories db))]
      (println category)
      {:http-xhrio {:method          :get
                    :uri             (str "/products/" category-id)
                    :format          (ajax/transit-request-format)
                    :response-format (ajax/transit-response-format)
                    :on-success      [:select-category-success category]
                    :on-failure      []}
       :db         (-> db
                       (assoc-in [:ui :selected-category] category)
                       (assoc :category category)
                       (assoc-in [:products-by-category category] :loading))})))

(rf/reg-sub
  :categories
  (fn [db _]
    (:categories db)))

(rf/reg-sub
  :category
  (fn [db _]
    (:category db)))

(rf/reg-sub
  :products-from-selected-category
  (fn [db _]
    ((:products-by-category db) (:category db))))

(rf/reg-sub
  :loading-categories?
  (fn [db _]
    (= :loading (:categories db))))


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
  [db product]
  (update-in db [:cart product] inc-if-nil-1))

(rf/reg-event-db
  :add-to-cart
  [state/check-spec-interceptor]
  (fn [db [_ product]]
    (add-to-cart db product)))

(defn calculate-average
  [review-entries-map]
  (average (clj->js (vals review-entries-map))))

(defn review
  [app product]
  (let [reviews (-> (:stars app)
                    (get product))]
    (if (nil? reviews)
      "-"
      (calculate-average reviews))))

(rf/reg-sub
  :review
  (fn [db [_ product]]
    (review db product)))

(defn review-by-user
  [app product]
  (get-in app [:stars product (:logged-user app)]))

(rf/reg-sub
  :review-by-user
  (fn [db [_ product]]
    (review-by-user db product)))

(defn give-review
  [app product review-score]
  (update-in app [:stars product] #(assoc % (:logged-user app) review-score)))

(rf/reg-event-db
  :give-review
  []
  (fn [db [_ product review-score]]
    (println product review-score)
    (give-review db product review-score)))

(defn give-review!
  [product review-score]
  (state/update-state!
    give-review product review-score))

(defn cart-size
  [app]
  (reduce (fn [val [product size]] (+ val size)) 0 (:cart app)))

(rf/reg-sub
  :cart-size
  (fn [db _]
    (cart-size db)))

(defn cart
  [app]
  (:cart app))

(rf/reg-sub
  :cart
  (fn [db _]
    (cart db)))

(defn filter-zero-or-blanks
  [m]
  (reduce (fn [acc [k v]] (if (or (= "" v) (= 0 v))
                                  acc
                                  (assoc acc k v)
                                  )) {} m))

(defn update-cart
  [app cart]
  (println "updating " cart)
  (let [filtered-cart (filter-zero-or-blanks cart)]
    (assoc app :cart filtered-cart)))

(rf/reg-event-db
  :update-cart
  (fn [db [_ cart]]
    (update-cart db cart)))







