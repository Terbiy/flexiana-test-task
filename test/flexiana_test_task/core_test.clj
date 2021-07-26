(ns flexiana-test-task.core-test
  (:require [clojure.test :refer :all]
            [flexiana-test-task.scramble :refer :all]))

(deftest expected-cases
  (testing "Successful inclusion of substring"
           (is (true? (scramble? "rekqodlw" "world")))
           (is (true? (scramble? "cedewaraaossoqqyt" "codewars"))))

  (testing "Failing inclusion of substring"
           (is (false? (scramble? "katas" "steak")))
           (is (false? (scramble? "aaaccc" "bbbddd"))))

  (testing "Empty strings"
           (is (true? (scramble? "hello" ""))
               "An empty string is treated as composable from anything")

           (is (true? (scramble? "" ""))
               "Empty string even could be composed of another empty string")

           (is (false? (scramble? "" "anything"))
               "Empty string can't compose anything meaningful")))

(deftest incorrect-input
  (testing
    "Strings containing something other than letters from a to z are not allowed"
    (is (thrown? AssertionError (scramble? "ASDASD" "ASD") "")
        "Even capital letters are not supported as per the task description")

    (is (thrown? AssertionError (scramble? "32312" "999"))
        "Numbers are not supported")

    (is
      (thrown? AssertionError (scramble? "👨‍👩‍👦" "👨"))
      "This strictness saves us from handling obscure UTF composed characters")

    (is (thrown? AssertionError (scramble? "asd123" "asd"))
        "An incorrect source string leads to exception")

    (is (thrown? AssertionError (scramble? "asd" "asd123"))
        "An incorrect substring candidate leads to exception"))

  (testing
    "Any type of non-string parameter leads to an exception."
    (let [DIFFERENT_NON_STRING_VALUES [nil 1 1/2 1.0 36786883868216818816N
                                       3.14159265358M \a :a '() [] {} #{}]
          PAIRS_OF_DIFFERENT_NON_STRING_VALUES (for
                                                 [a DIFFERENT_NON_STRING_VALUES
                                                  b DIFFERENT_NON_STRING_VALUES]
                                                 [a b])
          throws? (fn [function arguments]
                    (try (do (apply function arguments) false)
                         (catch AssertionError _ true)))]

      (is (true? (->> (map #(throws? scramble? %)
                        PAIRS_OF_DIFFERENT_NON_STRING_VALUES)
                      (every? true?)))))))
