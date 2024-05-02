(ns centripetal.http-test
  (:require [centripetal.http :refer :all]
            [centripetal.main :as main]
            [centripetal.generator :as gen]
            [io.pedestal.http :as server]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is testing]]
            [io.pedestal.test :as r]
            [cheshire.core :as json]
            [clojure.walk :as walk]))

(defn mock-db [content]
  (let [system (component/start
                (main/create-system
                 {:env :test
                  :conn content}))]
    (-> system :http :server ::server/service-fn)))

(defn read-json [response]
  (-> response :body json/decode walk/keywordize-keys))

(deftest single-indicator-test
  (testing "retrieve a single indicator by ID"
    (let [data       (gen/generate-compromises)
          id         (-> data first :id)
          url        (str "/indicators/" id)
          service-fn (mock-db data)
          response (r/response-for service-fn :get url)]
      (is (= 200 (:status response)))
      (is (= id (:id (read-json response))))))

  (testing "not found"
    (let [data       (gen/generate-compromises)
          service-fn (mock-db data)
          response   (r/response-for service-fn :get "/indicators/id-not-found")]
      (is (= 404 (:status response))))))

(deftest multiple-indicators-test
  (testing "get all compromises"
    (let [data       (gen/generate-compromises 2)
          service-fn (mock-db data)
          response (r/response-for service-fn :get "/indicators")]
      (is (= 200 (:status response)))
      (is (= (map :id data)
             (map :id (read-json response))))))

  (testing "filter by matching type"
    (let [data       (gen/generate-compromises 2)
          service-fn (mock-db data)
          response (r/response-for service-fn :get (str "/indicators?type=" (-> data first :indicators first :type)))]
      (is (= 200 (:status response)))
      (is (= (-> data first :id)
             (-> (read-json response) first :id)))))

  (testing "no type matched"
    (let [data       (gen/generate-compromises 2)
          service-fn (mock-db data)
          response (r/response-for service-fn :get "/indicators?type=unknown-type")]
      (is (= 200 (:status response)))
      (is (empty? (read-json response))))))

(deftest search-indicator-test
  (testing "search"))

#_(let [data       (gen/generate-compromises)
      service-fn (mock-db data)
      response (r/response-for
                service-fn
                :post "/indicators/search"
                :body (json/encode {:a :b})
                :headers {"Content-Type" "application/json"})]
  response)
