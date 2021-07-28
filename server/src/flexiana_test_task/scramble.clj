(ns flexiana-test-task.scramble
  (:require [clojure.spec.alpha :as spec]))

(defn- enough-of-character-in-pool?
  [characters-pool [character demand]]
  (let [supply (get characters-pool character 0)] (<= demand supply)))

(defn- enough-characters-in-pool?
  [characters-pool characters-demand]
  (->> (map (partial enough-of-character-in-pool? characters-pool)
         characters-demand)
       (every? true?)))

(spec/def ::scramblable-string
  #(boolean (and (string? %) (re-matches #"[a-z]*" %))))

(defn scramble?
  [source-string substring-candidate]
  {:pre [(spec/valid? ::scramblable-string source-string)
         (spec/valid? ::scramblable-string substring-candidate)]}
  (enough-characters-in-pool? (frequencies source-string)
                              (frequencies substring-candidate)))
