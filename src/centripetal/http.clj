(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as pedestal.http]
   [io.pedestal.http.route :as route]
   [ring.util.response :as resp]))

(defn indicator [req])

(defn indicators [req])

(defn search-indicator [req])

(defn with-db [db]
  {:name ::with-db
   :enter (fn [context]
            (assoc context :db db))})

;; TODO inject database interceptor
(defn routes [db]
  [[["/" ^:interceptors [(with-db db)]
     ["/indicators"        {:get indicators}]
     ["/indicators/:id"    {:get indicator}]
     ["/indicators/search" {:post search-indicator}]]]])

(defrecord HTTPServer [db]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :http-server
     (->
      {:env :dev
       ::http/routes (routes db)
       ::http/join? false
       ::http/port 8080}
      http/default-interceptors
      http/create-server
      http/start)))
  (stop [this]))
