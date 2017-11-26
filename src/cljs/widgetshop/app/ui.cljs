(ns widgetshop.app.ui
  (:require [widgetshop.app.state :as state]))

(defn switch-page!
  [page]
  (set! js/window.location.href (str "#/" page)))

(defn current-page
  [app]
  (-> (:ui app) :page))

(defn set-page
  [app page-kw]
  (assoc-in app [:ui :page] page-kw))

(defn set-page!
  [page-kw]
  (state/update-state!
    set-page
    page-kw))

(defn selected-product
  [app]
  (-> (:ui app) :selected-product))

(defn select-product
  [app category]
  (-> (assoc-in app [:ui :selected-product] category)
      (assoc-in [:ui :page] :product-page)))

(defn select-product!
  [product]
  (state/update-state!
    select-product
    product)
  (switch-page! "product"))