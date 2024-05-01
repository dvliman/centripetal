(ns centripetal.http-test
  (:require [centripetal.http :refer :all]
            [centripetal.main :as main]
            [io.pedestal.http :as server]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is testing]]
            [io.pedestal.test :as r]))

(def test-overrides
  {:env :test})

;; warning: modifying live system
(defn mock-db [content f]
  (let [system (component/start (main/create-system test-overrides))
        system (update-in system [:db :conn] (constantly content))]
    (as-> (-> system :http :server ::server/routes ::server/service-fn) service-fn
      (service-fn f))))

(deftest indicator-test
  #_(testing "retrieve a single indicator by ID"
    (mock-db
     [{:indicators []}]
     )))


#_(let [system (component/start (main/create-system test-overrides))
      system (update-in system [:db :conn] (constantly {:a :b}))]
  (as-> (-> system :http :server ) service-fn
    service-fn
    ))

(let [system (component/start (main/create-system test-overrides))
      system (update-in system [:db :conn] (constantly {:a :b}))]
  (as-> (-> system :http :server ::server/service-fn) service-fn
    (r/response-for service-fn :get "/indicators?q=yay")))
