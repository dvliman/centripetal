(ns centripetal.main
  (:require
   [com.stuartsierra.component :as component]
   [centripetal.db :as db]
   [environ.core :refer [env]]))

(def system nil)

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. (fn []
            (when system
              (component/stop system)))))

(defn create-system []
  (component/system-using
   (component/system-map
    :db (db/map->Database {:db-filepath (env :db-filepath)})
    :http nil)
   {:http [:database]}))

(defn -main [& args]
  (alter-var-root #'system (constantly create-system)))
