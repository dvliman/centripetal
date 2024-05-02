(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [cheshire.core :as json]))

(defn json-response [body]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(defn bad-response [body]
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(defn not-found [body]
  {:status 404
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(def a (atom nil))

(defn capture [x]
  (reset! a x)
  x)

(defn indicator [{:keys [conn]}]
  (fn [{{:keys [id]} :path-params}]
    (if-let [compromise (first (filter #(= (:id %) id) conn))]
      (json-response compromise)
      (not-found {:id id}))))

(defn match-type? [type]
  (fn [compromise]
    (contains? (set (map :type (:indicators compromise))) type)))

(defn indicators [{:keys [conn]}]
  (fn [context]
    (capture context)
    (if-let [type (some-> context :params :type)]
      (json-response (filter (match-type? type) conn))
      (json-response conn))))

(defn search-indicators [{:keys [conn]}]
  (fn [context]
    (prn "search")
    (capture context)
    {:status 200 :headers {} :body "c"}))

(defn routes [db]
  #{["/indicators"        :get (indicators db) :route-name :indicators]
    ["/indicators/:id"    :get (indicator db) :route-name :indicator]
    ["/indicators/search" :post [(body-params/body-params) (search-indicators db)] :route-name :search-indicators]})

(defn production? [config]
  (not (= :test (:env config))))

(defrecord HTTP [db config]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :server
     (cond->
      (server/default-interceptors
       (server/create-server
        {::server/routes (route/expand-routes (routes db))
         ::server/type :jetty
         ::server/join? false
         ::server/port 8080}))

       (production? config)
       server/start)))
  (stop [this]
    (server/stop (:server this))))
