# riemann-manifold

_This repository refers to a library to monitor asynchronous programs._

_Not to be confused with [Riemannian manifold](https://en.wikipedia.org/wiki/Riemannian_manifold)._

![Clojars Project](http://clojars.org/riemann-manifold/latest-version.svg)

A library to monitor Manifold streams with Riemann, and more!

## Usage

You can read
[documention](https://fundingcircle.github.io/riemann-manifold) for
all six-or-so functions, but these example are probably all you need.

Open a Riemann connection:
````clojure
(require '[riemann.client :as r])

(def c (r/tcp-client {:host "localhost"}))
````

Send metrics to Riemann by putting events on an Manifold stream.

````clojure
(require '[manifold.stream :as s])
(require '[riemann-manifold.core :as rm])


(def things (s/stream))

(rm/consume-metrics c things)

@(s/put! things {:service "things"
                 :metric 1
                 :state "questionable"
                 :ttl 10000})

(pprint @(r/query c "service = \"things\""))
;; ({:host "someshot",
;;   :service "things",
;;   :state "questionable",
;;   :description nil,
;;   :metric 1,
;;   :tags nil,
;;   :time 1444840430,
;;   :ttl 10000.0})
````

Send metrics about a Manifold stream to Riemann.

````clojure
(def a (s/stream))

(rm/instrument a c "a-stream" 10 10 "demo")

@(r/query c "service =~ \"a-stream%\"")

(pprint @(r/query c "service = \"things\""))
;; ({:host "pepsi",
;;   :service "a-stream\nbuffer-capacity",
;;   :state "open",
;;   :description nil,
;;   :metric 0,
;;   :tags ["manifold-stream" "demo"],
;;   :time 1444840968,
;;   :ttl 10000.0}
;;  {:host "pepsi",
;;   :service "a-stream\npending-takes",
;;   :state "open",
;;   :description nil,
;;   :metric 0,
;;   :tags ["manifold-stream" "demo"],
;;   :time 1444840968,
;;   :ttl 10000.0}
;;  {:host "pepsi",
;;   :service "a-stream pending-puts",
;;   :state "open",
;;   :description nil,
;;   :metric 0,
;;   :tags ["manifold-stream" "demo"],
;;   :time 1444840968,
;;   :ttl 10000.0}
;;  {:host "pepsi",
;;   :service "a-stream buffer-size",
;;   :state "open",
;;   :description nil,
;;   :metric 0,
;;   :tags ["manifold-stream" "demo"],
;;   :time 1444840968,
;;   :ttl 10000.0})
````

Send the result of applying a function to each message in a stream to Riemann.

````clojure
(def b (s/stream))

(def b' (rm/tap b c "incremented" #(hash-map :metric (inc %)) 10000))

(s/put! b 1)
; << true >>

@(s/take! b')
; 1

(pprint @(r/query c "service = \"incremented\""))

;; ({:host "pepsi",
;;   :service "incremented",
;;   :state "open",
;;   :description nil,
;;   :metric 2,
;;   :tags nil,
;;   :time 1444841173,
;;   :ttl 10000.0})
````

## License

Copyright © 2015 Funding Circle

Distributed under the BSD 3-Clause License.
