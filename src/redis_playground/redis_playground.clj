(ns redis-playground.redis-playground
  (:require [taoensso.carmine :as car :refer (wcar)]
            [carmine-streams.core :as cs]
            [taoensso.timbre :as log]
            [clojure.repl :refer :all])

  (:gen-class))


(def server1-conn {:pool {} :spec {:uri "redis://localhost:6379/"}})
(defmacro wcar* [& body] `(wcar server1-conn ~@body))

(wcar* (car/ping))

(comment
  (wcar*
    (car/ping)
    (car/set "foo" "bar")
    (car/get "foo"))


  ; carmine streams
  (def conn-opts {})

  (def stream (cs/stream-name "sensor-readings"))        ;; -> stream/sensor-readings
  (def group (cs/group-name "persist-readings"))         ;; -> group/persist-readings
  (def consumer (cs/consumer-name "persist-readings" 0))

  (defn xadd-map [& args]
    (apply car/xadd (concat (butlast args) (reduce into [] (last args)))))

  (let [args [(cs/stream-name "maps") "*" {:foo "bar" :event-type :open-event}]]
    (concat (butlast args) (reduce into [] (last args))))

  (car/wcar conn-opts (cs/xadd-map (cs/stream-name "maps") "*" {:foo "bar"}))
  (car/wcar conn-opts (cs/xadd-map (cs/stream-name "maps") "*" {:foo "bar2"}))
  (car/wcar conn-opts (cs/xadd-map (cs/stream-name "maps") "*" {:foo "bar3"}))
  (car/wcar conn-opts (cs/xadd-map (cs/stream-name "maps") "*" {:foo "bar4"}))

  (wcar* (car/xadd (cs/stream-name "maps") "*"
           :event [{:event-key "alpha"} {:event-type :add-event}]))

  (let [[[_stream messages]] (car/wcar conn-opts (car/xread :count 100 :streams (cs/stream-name "maps") "0-0"))]
    (map (fn [[_id kvs]] (cs/kvs->map kvs))
      messages))



  ; from https://tirkarthi.github.io/programming/2018/08/17/redis-streams-clojure.html


  (wcar* (car/xadd :chennai "*" :temperature 26 :humidity 10))
  (wcar* (car/xadd :chennai "*" :temperature 22 :humidity 15))

  (let [[[_stream messages]] (car/wcar conn-opts (car/xread :count 100 :streams :chennai "0-0"))]
    (map (fn [[_id kvs]] (cs/kvs->map kvs))
      messages))

  (wcar* (car/xrange :chennai "-" "+"))
  (wcar* (car/xrange :chennai "-" "+" :count 3))
  (wcar* (car/xrange :chennai "1618110264963-0" "+"))

  (doc car/xgroup)
  (doc car/xreadgroup)

  (wcar* (car/xgroup :create :chennai :mygroup "$"))
  (wcar* (car/xreadgroup :block 0 :group :mygroup :alice
           :count 1 :streams :chennai ">"))

  (wcar* (car/xpending :chennai :mygroup))
  (wcar* (car/xpending :chennai :mygroup "-" "+" 10))

  ())






; for our rocky-road components
(defn exit-point [channel message]
  (log/info "REDIS PUBLISH =====> " channel)
  (wcar* (car/xadd (cs/stream-name channel) "*" :event message)))

(exit-point "maps" [{:event-key "beta"} {:event-type :add-event}])





(defn entry-point [channel])


; (how) can we do a "ktable"?


(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))
