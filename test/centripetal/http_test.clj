(ns centripetal.http-test
  (:require [centripetal.http :refer :all]
            [centripetal.main :as main]
            [centripetal.generator :as gen]
            [io.pedestal.http :as server]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is testing]]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as r]))

(def test-overrides
  {:env :test})

;; warning: modifying live system
(defn mock-db [content]
  (let [system (component/start (main/create-system test-overrides))
        system (update-in system [:db :conn] (constantly content))]
    (-> system :http :server ::server/service-fn)))
(-> (component/start (main/create-system test-overrides))
    :http :server

    )
(deftest single-indicator-test
  (testing "retrieve a single indicator by ID"
    (let [data (gen/generate-compromises)
          url1 (str "/indicator/" (-> data first :id))
          url2 "/indicator/not-found"

          service-fn (mock-db data)]
      (r/response-for service-fn :get url1))))

(let [data (gen/generate-compromises)
          url1 (str "/indicators/" (-> data first :id))
          url2 "/indicators/not-found"

          service-fn (mock-db data)]
  (r/response-for service-fn :get url1))
#_(let [system (component/start (main/create-system test-overrides))
       system (update-in system [:db :conn] (constantly {:a :b}))]
  (as-> (-> system :http :server ::server/service-fn) service-fn
    (r/response-for service-fn :get "/indicators?q=yay")))
d925AdgG4W5BGt2rb8uQ7LLvop
@centripetal.http/a
