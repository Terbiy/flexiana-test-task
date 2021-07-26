(ns flexiana-test-task.core
  (:require [flexiana-test-task.server :refer [start-server]])
  (:gen-class))

(defn -main [] (start-server))

