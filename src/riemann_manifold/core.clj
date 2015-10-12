(ns riemann-manifold.core
  (:require [manifold.stream :as s]
            [riemann.client :as r]))

(def c' (r/tcp-client {:host "localhost"}))

(defn- timestamp []
  (System/currentTimeMillis))

(defn stream-state
  "Gives a string describing the state of a stream."
  [src]
  (cond
    (s/drained? src) "drained"
    (s/closed? src) "closed"
    :default "open"))


(defn stream-metrics
  "Filters a stream description only include those with numeric
  values."
  [src]
  (into {} (filter #(number? (second %))
                   (s/description src))))


(defn stream-events
  "Returns a collection of Riemann events for a stream. Each numeric
  metric will be a separate event, with state for each event set to
  that of the source stream."

  [service src ttl tags]

  (let [state (stream-state src)
        now (timestamp)]
    (map (fn [[k v]]
           {:service (str service " " (name k))
            :state state
            :time now
            :ttl ttl
            :tags tags
            :metric v})
         (stream-metrics src))))


(defn- blocking-sender
  ([f c ms] (blocking-sender f c 1000 ms))
  ([f c t ms] (deref (f c ms) t nil)))

(defn instrument-stream
  "Send stats from a manifold executor to Riemann. This takes a
  stream, and puts it onto an executor that will send stats to
  Riemann."

  [src c service period ttl & tags]

  (let [send! (partial blocking-sender r/send-events c)
        f (fn []
            (stream-events service
                           src
                           ttl
                           (conj tags "manifold-stream")))
        events (s/periodically period f)]

    (s/consume send! events)))



(defn tap
  "Use the contents of a stream to provide metrics to Riemann."
  [src c service f ttl & tags]
  (let [feed (s/stream)
        send! (partial blocking-sender r/send-event c)
        wrapf (partial merge {:service service
                               :ttl ttl
                               :state (stream-state src)
                               :time (timestamp)})]

    (s/connect src feed)
    (s/consume (comp send! wrapf f) feed)))
