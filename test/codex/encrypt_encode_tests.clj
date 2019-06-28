(ns codex.encrypt-encode-tests
  (:require [clojure.test :refer :all]
            [codex.core :as codex])
  (:import (crypto Key$ExpandedKey)))


(deftest test-expand-key
  (let [k1 (codex/expand-pass :sha128+hmac256 "secret")
        k2 (codex/expand-pass :sha256+hmac512 "secret")]

    (is (instance? Key$ExpandedKey k1))
    (is (instance? Key$ExpandedKey k2))))

(deftest test-encrypt-decrypt-gcm
  (let [encoder (->>
                  (codex/kryo-encoder)                              ;; convert to bytes
                  (codex/lz4-encoder)                               ;; compress
                  (codex/crypto-encoder                             ;; encrypt
                    :aes-cbc-hmac
                    (codex/expand-pass :sha256+hmac512 "secret")))

        data {:a 1 :b {:c [1 2 3]}}

        encrypted-data (codex/encode encoder data)
        decoded-data (codex/decode encoder encrypted-data)]

    (is
      (= data decoded-data))))

(deftest test-encrypt-decrypt-default-encoder
  (let [encoder (codex/default-encoder (codex/expand-pass :sha256+hmac512 "secret"))
        data {:a 1 :b {:c [1 2 3]}}

        encrypted-data (codex/encode encoder data)
        decoded-data (codex/decode encoder encrypted-data)]

    (is
      (= data decoded-data))))
