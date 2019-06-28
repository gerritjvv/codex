(ns codex.kryo-encode-tests
  (:require [clojure.test :refer :all]
            [codex.core :refer [kryo-encoder encode decode]]
            [codex.core :as codex]))



(deftest test-kryo-map
  (let [encoder (kryo-encoder)
        m {::a 1 :b 2 "c" "123"}

        m' (encode encoder m)

        m'' (decode encoder m')]
    (prn "Byte count: " (count m'))
    m''))

(deftest test-kryo
  (let [encoder (kryo-encoder)


        data [{:a 1 :b 2}
              [1 2 3]
              ['1 '2 '3 '4]
              '1
              :a
              "Hi"
              1
              1.0
              (float 1.0)
              \a
              '(1 2 3)
              (seq [1 2 3])]]

    (doseq [v data]
      (let [v2 (decode encoder (encode encoder v))]

        (prn {:v (class v) :v2 v2})
        (cond
          (coll? v) (is (= (sort v) (sort v2)))

          :else (is (= v v2)))))))


(defrecord MyRecord [a b])


(deftest test-encode-record

  (let [_ (codex/register-record! MyRecord)

        data (->MyRecord 1 2)
        encoder (codex/default-encoder (codex/expand-pass-default "secret"))
        encoded-data (codex/encode encoder data)
        decoded-data (codex/decode encoder encoded-data)]

    (is
      (= data
         decoded-data))))