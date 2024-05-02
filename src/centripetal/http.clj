(ns centripetal.http
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [cheshire.core :as json]
   [centripetal.generator :as gen]
   [clojure.set :as set]))

(defn json-response [body]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(defn bad-request [body]
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(defn not-found [body]
  {:status 404
   :headers {"Content-Type" "application/json"}
   :body (json/encode body)})

(defn indicator [{:keys [conn]}]
  (fn [{{:keys [id]} :path-params}]
    (if-let [compromise (first (filter #(= (:id %) id) conn))]
      (json-response compromise)
      (not-found {:id id :reason "not found"}))))

(defn match-type? [type]
  (fn [compromise]
    (contains? (set (map :type (:indicators compromise))) type)))

(defn indicators [{:keys [conn]}]
  (fn [context]
    (if-let [type (some-> context :params :type)]
      (json-response (filter (match-type? type) conn))
      (json-response conn))))

;; only search top-level keys in the indicator of compromise document
(defn search [params]
  (fn [compromise]
    (= (count params) ;; AND-ed the terms (term is field=value match on the document)
       (count
        (set/intersection
         (set (seq params))
         (set (seq (select-keys compromise gen/searchable-compromise-fields))))))))

(defn search-indicators [{:keys [conn]}]
  (fn [{:keys [json-params] :as context}]
    (cond
      (nil? (seq json-params))
      (bad-request {:reason "search term is required"})

      (not (gen/valid-search-params? json-params))
      (bad-request {:reason "search term contains extra or missing keys"})

      :else
      (json-response (filter (search json-params) conn)))))

(defn routes [db]
  #{["/indicators/search" :post [(body-params/body-params) (search-indicators db)] :route-name :search-indicators]
    ["/indicators"        :get (indicators db) :route-name :indicators]
    ["/indicators/:id"    :get (indicator db) :route-name :indicator]})

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
         ::server/router :linear-search
         ::server/type :jetty
         ::server/join? false
         ::server/port (:port config)}))

       (production? config)
       server/start)))
  (stop [this]
    (server/stop (:server this))))
