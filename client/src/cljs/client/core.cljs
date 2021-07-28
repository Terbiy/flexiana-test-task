(ns client.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [reagent.session :as session]
            [reitit.frontend :as reitit]
            [clerk.core :as clerk]
            [accountant.core :as accountant]
            [cljs.reader :as reader]
            [cljs.pprint :refer [pprint]]))

;; -------------------------
;; Routes

(def router (reitit/router [["/" :index]]))

;; -------------------------
;; Home page

(defn reflect-server-response-in-UI
  [response scramble-results-state]
  (let [scramble-results (:scramble-results response)
        new-scramble-results-state (if scramble-results :positive :negative)]
    (reset! scramble-results-state new-scramble-results-state)))

(defn call-for-scramble
  [event requesting-results scramble-results unset-scramble-results
   source-string substring-candidate]
  (.preventDefault event)

  (reset! requesting-results true)
  (unset-scramble-results)

  (let [scramble-url (str "http://localhost:8890/scramble?source-string="
                            source-string
                          "&substring-candidate=" substring-candidate)]
    (-> (.fetch js/window scramble-url)
        (.then #(.text %))
        (.then reader/read-string)
        (.then #(reflect-server-response-in-UI % scramble-results))
        (.finally #(reset! requesting-results false)))))

(defn update-input
  [event input-state]
  (let [value (-> event
                  .-target
                  .-value)
        cleaned-value (apply str (re-seq #"[a-z]" value))]
    (reset! input-state cleaned-value)))

(defn home-page
  []
  (let [source-string (reagent/atom "")
        substring-candidate (reagent/atom "")
        requesting-results (reagent/atom false)
        scramble-results (reagent/atom :unset)
        unset-scramble-results #(reset! scramble-results :unset)
        update-input (fn [event input-state]
                       (unset-scramble-results)
                       (update-input event input-state))
        call-for-scramble #(call-for-scramble %
                                              requesting-results
                                              scramble-results
                                              unset-scramble-results
                                              @source-string
                                              @substring-candidate)]
    (fn []
      [:section.main [:h1 "Flexiana Test Task"]
       [:form {:class "scramble-form", :on-submit call-for-scramble}
        [:label {:class "scramble-form__field"} "Source String"
         [:input
          {:type :text,
           :value @source-string,
           :on-change #(update-input % source-string)}]]
        [:label {:class "scramble-form__field"} "Substring Candidate"
         [:input
          {:type :text,
           :value @substring-candidate,
           :on-change #(update-input % substring-candidate)}]]
        [:input
         {:type :submit,
          :value (if @requesting-results "Loading..." "Scramblable?"),
          :disabled @requesting-results}]]
       (cond
         (= :positive @scramble-results)
           [:p
            "You can combine substring from the letters of the source string."]
         (= :negative @scramble-results)
           [:p
            "It's not possible to rearrange source string letters so that you'll receive the substring."]
         :else nil)])))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route] (case route :index #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (rdom/render [home-page] (.getElementById js/document "app")))

(defn init!
  []
  (clerk/initialize!)
  (accountant/configure-navigation!
    {:nav-handler (fn [path]
                    (let [match (reitit/match-by-path router path)
                          current-page (:name (:data match))
                          route-params (:path-params match)]
                      (reagent/after-render clerk/after-render!)
                      (session/put! :route
                                    {:current-page (page-for current-page),
                                     :route-params route-params})
                      (clerk/navigate-page! path))),
     :path-exists? (fn [path] (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
