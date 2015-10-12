(ns riemann-manifold.core-test
  (:require [clojure.test :refer :all]
            [manifold.stream :as s]
            [riemann-manifold.core :refer :all]))

(deftest stream-state-test
  (testing "a new stream"
    (is (= "open" (stream-state (s/stream)))))

  (testing "a drained stream"
    (let [s' (s/stream)]
      (s/close! s')
      (is (= "drained" (stream-state s')))))

  (testing "a closed stream"
    (let [s' (s/stream)]
      (s/put! s' :x)
      (s/close! s')
      (is (= "closed" (stream-state s'))))))
