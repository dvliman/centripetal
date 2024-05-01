(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
   [io.pedestal.interceptor :as interceptor]))

(defn indicator [context]
  (prn "single indicator")
  (assoc context
         :response {:status 200 :headers {} :body "a"}))

(def d (atom nil))
(defn indicators [context]
  (reset! d (first (:db context)))
  {:status 200 :headers {} :body "david"})

#_(defn indicators [db]
  (fn [context]
    (reset! c {:context context :db db})
    {:status 200 :headers {} :body "david"}))

(defn search-indicators [context]
  (prn "search-indicators")
  (assoc context
         :response {:status 200 :headers {} :body "c"}))

(def routes
  #{["/indicators"        :get indicators         :route-name :indicators]
    ["/indicators/:id"    :get indicator          :route-name :indicator]
    ["/indicators/search" :post search-indicators :route-name :search-indicators]})

(defrecord HTTP [db config]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :server
     (cond->
      (server/create-server
       {::server/routes (route/expand-routes routes)
        ::server/type :jetty
        ::server/join? false
        ::server/port 8080
        ::server/interceptors [(interceptor/interceptor
                                {:name ::db-interceptor
                                 :enter (fn [context]
                                          (assoc context :db db))})]})
       true
       server/default-interceptors

       (not (= :test (:env config)))
       server/start)))
  (stop [this]
    (server/stop (:server this))))
