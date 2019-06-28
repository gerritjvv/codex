(ns codex.core

  (:require [codex.util :as util])
  (:import (crypto Key$ExpandedKey Key$KeySize)
           (codex.encode Encoder KryoEncoder CryptoEncoder Lz4Encoder)
           (clojure.lang PersistentArrayMap Keyword Symbol PersistentHashMap PersistentHashSet PersistentList PersistentVector BigInt PersistentVector$ChunkedSeq)
           (codex.serializers PersistentArrayMapSerde KeywordSerde PersistentMapSerde SymbolSerde SeqSerde PersistentHashSetSerde PersistentListSerde PersistentVectorSerde BigIntSerde PersistentRecordSerde)
           (com.esotericsoftware.kryo Serializer Registration)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; expanded keys
;;;; use (expand-pass :sha256+hmac512 "mypass")
(defmulti expand-pass (fn [t pass] t) :default :sha256+hmac512)

(defmethod expand-pass :sha128+hmac256 [_ pass]
  (.genKeysHmacSha (Key$KeySize/AES_128) ^"[B" (util/-as-bytes pass)))

(defmethod expand-pass :sha256+hmac512 [_ pass]
  (.genKeysHmacSha (Key$KeySize/AES_256) ^"[B" (util/-as-bytes pass)))


(defn expand-pass-default [pass]
  (expand-pass :sha256+hmac512 pass))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Crypto encoder wrapper


;Create an encoder that encrypt using AES+CBC or AES+GCM.
;           The bits used depends on the key:
;            For AES+CBC+HMAC512 use a of :sha256+hmac512
;            For AES+GCM  use :sha128+hmac256
(defmulti crypto-encoder (fn [t k encoder] t))

(defmethod crypto-encoder :aes-cbc-hmac [_ key encoder]
  (CryptoEncoder/getCBCHmacInstance ^Key$ExpandedKey key encoder))

(defmethod crypto-encoder :aes-gcm [_ key encoder]
  (CryptoEncoder/getGCMInstance ^Key$ExpandedKey key encoder))

(defn lz4-encoder ^Encoder [^Encoder encoder]
  (when (instance? CryptoEncoder encoder)
    (throw (RuntimeException.
             (str "Never compress encrypted data."
                  "https://security.stackexchange.com/questions/19969/encryption-and-compression-of-data"))))

  (Lz4Encoder/getEncoder encoder))

(defn kryo-encoder ^Encoder []
  (KryoEncoder/DEFAULT))

(defn default-encoder
  "Returns an encoder that kryo-encodes, then lz4 compresses and then encrypts using EAS-CBC-HMAC
   the HMAC used 256 or 512 depends on the key passed in"
  [^Key$ExpandedKey key]
  (crypto-encoder :aes-cbc-hmac key (lz4-encoder (kryo-encoder))))

(defn encode [^Encoder encoder obj]
  (.encodeObject encoder obj))

(defn decode [^Encoder encoder ^"[B" bts]
  (.decodeObject encoder bts))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Kryo Serde Registration Helpers

(defn register-serialiser!
  "Kryo serde registration"
  [^Class clazz ^Serializer serde]
  (KryoEncoder/register clazz serde))

(defn register-class!
  "Kryo serde registration"
  [clazz]
  (KryoEncoder/register clazz))

(defmacro register-record! [^Class r]
  `(KryoEncoder/register ~r
                         (PersistentRecordSerde. ~(symbol (str "map->" r)))))

(defn register-registration
  "Kryo serde registration"
  [^Registration reg]
  (KryoEncoder/register reg))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Register kryo serdes for
;;;; clojure collections and standard classes

(defn register-serialisers!
  "Register all the Kryo Clojure Serdes
   Is called as part of this namespace"
  []
  (let [serdes [
                [Symbol (SymbolSerde.)]
                [Keyword (KeywordSerde.)]
                [PersistentArrayMap (PersistentArrayMapSerde.)]
                [PersistentHashMap (PersistentMapSerde.)]
                [PersistentHashSet (PersistentHashSetSerde.)]
                [PersistentList (PersistentListSerde.)]
                [PersistentVector (PersistentVectorSerde.)]
                [PersistentList (PersistentListSerde.)]
                [PersistentVector$ChunkedSeq (SeqSerde.)]

                [BigInt (BigIntSerde.)]
                ]]
    (doseq[[clazz serde] serdes]
      (KryoEncoder/register ^Class clazz ^Serializer serde))))

;;; register default clojure serializers
(register-serialisers!)
