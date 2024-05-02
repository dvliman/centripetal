(ns centripetal.db
  (:require
   [cheshire.core :as json]
   [clojure.walk :as walk]
   [com.stuartsierra.component :as component]))

(defn test? [config]
  (= :test (:env config)))

(defrecord DB [config]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :conn ;; conn here refers to the entire db dataset
     (if (test? config)
       (:conn config) ;; mock data
       (-> config :file-path slurp json/decode walk/keywordize-keys))))
  (stop [_]))
