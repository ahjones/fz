(ns fz.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [response content-type status header]]
            [hiccup.core :as html]
            [hiccup.page :refer [html5]]
            [environ.core :refer [env]]
            [clojure.java.io :refer [file copy]])
  (:import [java.util.zip ZipFile]
           [java.util UUID])
  (:gen-class))

(def links (atom {}))

(defn unique [] (str (UUID/randomUUID)))

(defn create-link [request]
  (let [id (unique)]
    (swap! links assoc id nil)
    (-> (response "") (status 303) (header "Location" (str "/files/" id "/")))))

(defn generate-upload-page
  [id]
  (html5
   (html/html
    [:body
     [:form {:action (str "/files/" id) :enctype "multipart/form-data" :method "post"}
      [:input {:type "file" :name "blob"}]
      [:input {:type "submit" :value "Upload"}]]])))

(defn handle-get-file
  [id]
  (if-let [filename (@links id)]
    (file filename)
    (generate-upload-page id)))

(def generate-page
  (html5
   (html/html
    [:body
     [:form {:action "/generate" :method "post"}
      [:input {:type "submit" :value "New link please"}]]])))

(defn create-new-file
  []
  (java.io.File/createTempFile "fiz" "" (file "/tmp")))

(defn upload-file
  [id request]
  (let [tempfile (get-in request [:params :blob :tempfile])
        outfile (create-new-file)]
    (copy tempfile outfile)
    (swap! links assoc id (.getCanonicalPath outfile))
    (response "OK")))

(defroutes routes
  (GET "/" request (-> (response generate-page) (content-type "text/html; charset=utf-8")))
  (GET "/files/:id/" [id] (response (handle-get-file id)))
  (POST "/generate" request (create-link request))
  (POST "/files/:id" [id :as request] (upload-file id request)))

(def app (-> routes
             handler/site))
