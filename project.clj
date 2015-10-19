(defproject riemann-manifold "0.1.0"
  :description "Send metrics to Riemann with Manifold"
  :url "https://github.com/FundingCircle/riemann-manifold"
  :license {:name "BSD 3-Clause"
            :url "http://opensource.org/licenses/BSD-3-Clause"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [manifold "0.1.0"]
                 [riemann-clojure-client "0.4.1"]]

  :plugins [[codox "0.8.15"]]

  :codox {:src-dir-uri "http://github.com/FundingCircle/riemann-manifold/blob/master/"
          :src-linenum-anchor-prefix "L"})
