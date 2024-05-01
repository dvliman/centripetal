(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]))

(defn indicator [context]
  (prn "single indicator")
  (assoc context
         :response {:status 200 :headers {} :body "a"}))



(defn indicators [db]
  (fn [context]
    (reset! c {:context context :db db})
    {:status 200 :headers {} :body "david"}))

(defn search-indicators [context]
  (prn "search-indicators")
  (assoc context
         :response {:status 200 :headers {} :body "c"}))

#_(defn db-interceptor [db]
  (interceptor/interceptor
   {:name ::with-db
    :enter (fn [context]
             (assoc context :db db))}))

(defn routes [db]
  #{["/indicators"        :get (indicators db)         :route-name :indicators]
    ["/indicators/:id"    :get (indicator db)          :route-name :indicator]
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
