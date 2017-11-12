(ns widgetshop.db
  (:require #?(:clj  [clojure.spec.alpha :as s]

               :cljs [cljs.spec.alpha :as s])))

(s/def ::cart (s/every-kv ::product integer?))
(s/def ::id integer?)
(s/def ::name string?)
(s/def ::description string?)
(s/def ::category (s/nilable (s/keys :req-un [::id ::name ::description])))
(s/def ::categories (s/coll-of ::category))
(s/def ::price double?)
(s/def ::product (s/keys :req-un [::id ::name ::description ::price]))
(s/def ::loading #(= :loading %))
(s/def ::products (s/or :loading ::loading :products (s/coll-of ::product)))
(s/def ::products-by-category (s/every-kv ::category ::products))

(s/def ::page keyword?)
(s/def ::selected-category ::category)
(s/def ::selected-product ::product)
(s/def ::ui (s/keys :req-un [::selected-category ::page ::selected-product]))

(s/def ::db (s/keys :req-un [::cart ::category ::categories ::products-by-category]))

