(ns centripetal.db
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [com.stuartsierra.component :as component]))

(defrecord DB [file-path]
  component/Lifecycle
  (start [this]
    (assoc
     this
     :conn
     (-> file-path slurp json/decode walk/keywordize-keys)))
  (stop [this]))
