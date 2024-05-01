(ns centripetal.db
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [com.stuartsierra.component :as component]))

(defrecord Database [db-filepath]
  component/Lifecycle
  (start [this]
    (assoc this :db (with-open [reader (io/reader (io/file db-filepath))]
                      (-> reader slurp json/decode walk/keywordize-keys))))
  (stop [this]))
