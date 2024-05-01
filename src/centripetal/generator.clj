(ns centripetal.generator
  (:require
   [malli.generator :as gen]))

(def IndicatorOfCompromise
  [:map
   [:industries [:vector string?]]
   [:tlp string?]
   [:description string?]
   [:created string?] ;; TODO: utc string
   [:tags [:vector string?]]
   [:modified string?] ;; TODO: utc string
   [:author_name string?]
   [:public int?]
   [:extract_source [:vector string?]]
   [:references [:vector string?]]
   [:targeted_countries [:vector string?]]
   [:indicators
    [:vector
     [:map
      [:indicator string?]
      [:description string?]
      [:created string?] ;; TODO: utc string
      [:title string?]
      [:content string?]
      [:type
       [:enum "CVE"
        "FileHash-MD5"
        "FileHash-SHA1"
        "FileHash-SHA256"
        "IPv4"
        "URL"
        "YARA"
        "domain"
        "email"
        "hostname"]]
      [:id int?]]]]
   [:more_indicators boolean?]
   [:revision int?]
   [:adversary string?]
   [:id string?] ;; TODO: more id like
   [:name string?]])

(defn generate-compromises
  ([]
   (generate-compromises 1))
  ([how-many]
   (repeatedly how-many (constantly (gen/generate IndicatorOfCompromise)))))
