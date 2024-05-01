(ns centripetal.main
  (:require
   [com.stuartsierra.component :as component]
   [centripetal.db :as db]
   [centripetal.http :as http]
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
    :db (db/map->DB {:file-path (env :file-path "/Users/dliman/centripetal/resources/indicators.json")})
    :http (component/using
           (http/map->HTTP nil)
           [:db]))
   {:http [:db]}))

(defn -main [& args]
  (alter-var-root #'system (constantly (component/start (create-system))))
  :started)

(defn stop
  []
  (alter-var-root #'system
                  (fn [sys]
                    (when sys
                      (try
                        (component/stop sys)
                        (catch Throwable t
                          (prn t)
                          sys)))))
  :stopped)
