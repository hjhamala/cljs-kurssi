(ns widgetshop.app.state
  "Defines the application state atom"
  (:require [reagent.core :as r]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx path trim-v
                                   after debug dispatch]]
             [js.uuid :as uuid4]
             [widgetshop.db :as db]
            [cljs.spec.alpha :as s]))

(defonce app (r/atom {:cart {}
                      :categories :loading ;; list of product categories

                      ;; Loaded product listings keyd by selected category
                      :stars {}
                      :logged-user (uuid4/genuuid)
                      :products-by-category {}
                      :ui {:selected-category nil
                           :selected-product nil
                           :page :category-page}}))

(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (after (partial check-and-throw ::db/db)))

(reg-event-db
  :initialize
  (fn[db _]
    {:cart {}
     :categories :loading ;; list of product categories

     ;; Loaded product listings keyd by selected category
     :stars {}
     :logged-user (uuid4/genuuid)
     :products-by-category {}
     :ui {:selected-category nil
          :selected-product nil
          :page :category-page}}))

(defn update-state!
  "Updates the application state using a function, that accepts as parameters
  the current state and a variable number of arguments, and returns a new state.

  (defn set-foo [app n]
     (assoc app :foo n))

  (update-state! set-foo 1)"
  [update-fn & args]
  (swap! app
         (fn [current-app-state]
           (let [result (apply update-fn current-app-state args)]
             (if (s/valid? ::db/db result)
               result
               (do
                 (throw (ex-info (str "spec check failed: " (s/explain-str ::db/db result)) {}))))))))

