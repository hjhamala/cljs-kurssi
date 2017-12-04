(ns widgetshop.services.products
  (:require [widgetshop.components.http :refer [publish! transit-response bad-request]]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [clojure.java.jdbc :as jdbc]
            [widgetshop.db :as db]
            [clojure.spec.alpha :as s]
            [cognitect.transit :as transit]
            [clojure.java.io :as io]))

(defn fetch-products-for-category [db category]
  (into []
        (map #(update % :price double))
        (jdbc/query db [(str "SELECT p.id,p.name,p.description,p.price"
                             "  FROM product p"
                             "  JOIN product_category pc ON pc.product_id = p.id "
                             " WHERE pc.category_id = ?")
                        category])))

(defn fetch-product-categories [db]
  (jdbc/query db ["SELECT c.id, c.name, c.description FROM category c"]))

(defn check-id
  [db id]
  (jdbc/query db ["SELECT id FROM product where id = ?" id]))


(defn get-id
  [[{id :id} product _]]
  id)

(defrecord ProductsService []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc this ::routes
                (publish! http
                          (routes
                            (POST "/order" req
                              (let [body (transit/reader (:body req) :json)]
                                (if (->> (-> (transit/read body) :cart)
                                              (map get-id)
                                              (map (partial check-id db))
                                              (every? #(not (empty? %))))
                                         (transit-response {:message "ok"})
                                         {:headers {"Content-Type" "application/json+transit"}
                                          :status 400})))

                            (GET "/" [] {:status  200
                                         :headers {"Content-Type" "text/html"}
                                         :body    (io/file "resources/public/index.html")})
                            (GET "/categories" []
                              (transit-response
                                (fetch-product-categories db)))
                            (GET "/products/:category" [category]
                              (if (s/valid? ::db/id (try (Long/parseLong category) (catch Exception e nil)))
                                (transit-response
                                  (fetch-products-for-category db (Long/parseLong category)))
                                (bad-request)))))))
  (stop [{stop ::routes :as this}]
    (stop)
    (dissoc this ::routes)))
