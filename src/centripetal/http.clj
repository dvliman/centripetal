(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
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
    (reset! a conn)
    (if-let [compromise (first (filter #(= (:id %) id) conn))]
      (json-response compromise)
      (not-found {:id id}))))

(defn indicators [db]
  (fn [context]
    (prn "multiple")
    {:status 200 :headers {} :body "david"}))

(defn search-indicators [db]
  (fn [context]
    (prn "search")
    {:status 200 :headers {} :body "c"}))

(defn routes [db]
  #{["/indicators"        :get (indicators db) :route-name :indicators]
    ["/indicators/:id"    :get (indicator db) :route-name :indicator]
    ["/indicators/search" :post (search-indicators db) :route-name :search-indicators]})

(defrecord HTTP [db config]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :server
     (cond->
      (server/create-server
       {::server/routes (route/expand-routes (routes db))
        ::server/type :jetty
        ::server/join? false
        ::server/port 8080})
       true
       server/default-interceptors

       (not (= :test (:env config)))
       server/start)))
  (stop [this]
    (server/stop (:server this))))
