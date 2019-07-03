(ns codex.encrypt-encode-tests
  (:require [clojure.test :refer :all]
            [codex.core :as codex])
  (:import (crypto Key$ExpandedKey)))


(deftest test-expand-key
  (let [k1 (codex/derive-pass :sha128+hmac256 "test-salt" "secret")
        k2 (codex/derive-pass :sha256+hmac512 "test-salt" "secret")]

    (is (not (nil? k1)))
    (is (not (nil? k2)))))

(deftest test-encrypt-decrypt-gcm
  (let [encoder (->>
                  (codex/kryo-encoder)                              ;; convert to bytes
                  (codex/lz4-encoder)                               ;; compress
                  (codex/crypto-encoder                             ;; encrypt
                    :aes-cbc-hmac
                    (codex/derive-pass :sha256+hmac512 "salt" "secret")
                    ))

        data {:a 1 :b {:c [1 2 3]}}

        encrypted-data (codex/encode encoder data)
        decoded-data (codex/decode encoder encrypted-data)]

    (is
      (= data decoded-data))))

(deftest test-encrypt-decrypt-default-encoder
  (let [encoder (codex/default-encoder (codex/derive-pass :sha256+hmac512 "salt" "secret"))
        data {:a 1 :b {:c [1 2 3]}}

        encrypted-data (codex/encode encoder data)
        decoded-data (codex/decode encoder encrypted-data)]

    (is
      (= data decoded-data))))
