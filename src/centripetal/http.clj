(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [ring.util.response :as resp]))

(defn indicator [req])

(defn indicators [req])

(defn search-indicator [req])

(defn db-interceptor [db]
  {:name ::with-db
   :enter (fn [context]
            (assoc context :db db))})

(defn with-db [db]
  (fn [service-map]
    (update service-map ::server/interceptors (fn [interceptors] (conj interceptors (db-interceptor db))))))

(def routes
  #{["/indicators"        :get indicators :route-name :indicators]
    ["/indicators/:id"    :get indicator :route-name :indicator]
    ["/indicators/search" :post search-indicator :route-name :search-indicator]})

(defrecord HTTP [db]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :server
     (->
      {:env :dev
       ::server/routes routes
       ::server/type :jetty
       ::server/join? false
       ::server/port 8080}
      server/default-interceptors
      ((with-db db))
      server/create-server
      server/start)))
  (stop [this]
    (prn this)
    (server/stop (:server this))))
