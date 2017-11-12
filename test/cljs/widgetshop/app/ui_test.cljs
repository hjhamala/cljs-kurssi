(ns widgetshop.app.ui-test
  (:require [cljs.test :as test :refer-macros [deftest is testing]]
            [widgetshop.app.ui :as ui]))

(deftest current-page
  (testing "Returns current page"
    (let [db {:ui {:page :good-page}}
          expected :good-page]
      (is (ui/current-page db) expected))))

(deftest set-page
  (testing "Sets page correctly"
    (let [db {:ui {:page nil}}
          expected-db {:ui {:page :current-page}}]
      (is (ui/set-page db :current-page) expected-db)
      (is (ui/current-page (ui/set-page db :current-page)) :current-page))))

(deftest selected-product
  (testing "Return selected product"
    (let [db {:ui {:selected-product :good-product}}
          expected ::good-product]
      (is (ui/selected-product db) expected))))

(deftest select-product
  (testing "Sets product correctly"
    (let [db {:ui {:selected-product nil}}
          expected-db {:ui {:selected-product :good-product}}]
      (is (ui/select-product db :good-product) expected-db)
      (is (ui/selected-product (ui/select-product db :good-product)) :good-product))))

