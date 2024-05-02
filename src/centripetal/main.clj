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

(def default-config
  {:env       (env :environment "production")
   :file-path (env :file-path "/Users/dliman/centripetal/resources/indicators.json")})

(defn create-system
  ([]
   (create-system {}))
  ([overrides]
   (let [config (into default-config overrides)]
     (component/system-using
      (component/system-map
       :db (db/map->DB {:config config})
       :http (component/using (http/map->HTTP {:config config}) [:db]))
      {:http [:db]}))))

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
