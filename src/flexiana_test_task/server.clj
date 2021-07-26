(ns flexiana-test-task.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [flexiana-test-task.scramble :refer [scramble?]]
            [clojure.string :refer [includes?]]))

;; Scramble requests handling
(def SOURCE_STRING_URL_PARAMETER "source-string")
(def SUBSTRING_CANDIDATE_URL_PARAMETER "substring-candidate")

(defn- build-error [message] {:error {:message message}})

(defn- prepare-bad-request-response
  [message]
  {:status 400, :body (build-error message)})

(defn- format-invalid-parameter-value-message
  [parameter-name]
  (format "Parameter %s should consist only of lower case letters from a to z."
          parameter-name))

(defn- prepare-erroneous-scrambling-results-response
  [error-message]
  (cond (includes? error-message SOURCE_STRING_URL_PARAMETER)
          (prepare-bad-request-response (format-invalid-parameter-value-message
                                          SOURCE_STRING_URL_PARAMETER))
        (includes? error-message SUBSTRING_CANDIDATE_URL_PARAMETER)
          (prepare-bad-request-response (format-invalid-parameter-value-message
                                          SUBSTRING_CANDIDATE_URL_PARAMETER))
        :else {:status 500,
               :body (build-error "An unknown server error occurred.")}))

(defn- prepare-scrambling-results-response
  [source-string substring-candidate]
  (try {:status 200,
        :body {:scramble-results (scramble? source-string substring-candidate)}}
       (catch AssertionError e
         (prepare-erroneous-scrambling-results-response (.getMessage e)))))

(defn- prepare-url-parameter-missing-response
  [parameter-name]
  (prepare-bad-request-response (format "The %s URL-parameter is missing."
                                        parameter-name)))

(defn- prepare-scramble-response
  [request]
  (let [source-string (get-in request [:query-params :source-string])
        substring-candidate (get-in request
                                    [:query-params :substring-candidate])]
    (cond (nil? source-string) (prepare-url-parameter-missing-response
                                 SOURCE_STRING_URL_PARAMETER)
          (nil? substring-candidate) (prepare-url-parameter-missing-response
                                       SUBSTRING_CANDIDATE_URL_PARAMETER)
          :else (prepare-scrambling-results-response source-string
                                                     substring-candidate))))

;; Server settings
(def routes
  (route/expand-routes #{["/scramble" :get prepare-scramble-response :route-name
                          :scramble]}))

(def service-map {::http/routes routes, ::http/type :jetty, ::http/port 8890})

(defn start-server [] (http/start (http/create-server service-map)))

;; For interactive development
(defonce server (atom nil))

(defn start-dev
  []
  (reset! server
          (http/start (http/create-server (assoc service-map
                                            ::http/join? false)))))

(defn stop-dev [] (http/stop @server))

(defn restart [] (stop-dev) (start-dev))
