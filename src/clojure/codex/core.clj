(ns codex.core

  (:require [codex.util :as util])
  (:import (crypto Key$ExpandedKey Key$KeySize Key)
           (codex.encode Encoder KryoEncoder CryptoEncoder Lz4Encoder)
           (clojure.lang PersistentArrayMap Keyword Symbol PersistentHashMap PersistentHashSet PersistentList PersistentVector BigInt PersistentVector$ChunkedSeq LazySeq)
           (codex.serializers PersistentArrayMapSerde KeywordSerde PersistentMapSerde SymbolSerde SeqSerde PersistentHashSetSerde PersistentListSerde PersistentVectorSerde BigIntSerde PersistentRecordSerde)
           (com.esotericsoftware.kryo Serializer Registration)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;

(defmulti gen-expanded-key (fn [t k] t) :default :sha256+hmac512)

(defmethod gen-expanded-key :sha128+hmac256 [_ k]
  (.genKeysHmacSha (Key$KeySize/AES_128) ^"[B" (util/-as-bytes k)))

(defmethod gen-expanded-key :sha256+hmac512 [_ k]
  (.genKeysHmacSha (Key$KeySize/AES_256) ^"[B" (util/-as-bytes k)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; expanded keys
;;;; use (expand-pass :sha256+hmac512 "salt" "mypass")
;;;;     (expand-pass :sha128+hmac256 "salt" "mypass")

(defmulti derive-pass (fn [t salt pass] t) :default :sha256+hmac512)

(defmethod derive-pass :sha128+hmac256 [_ salt pass]
  (Key/deriveHmac256FromPass (util/-as-bytes salt) (util/-as-bytes pass)))

(defmethod derive-pass :sha256+hmac512 [_ salt pass]
  (Key/deriveHmac512FromPass (util/-as-bytes salt) (util/-as-bytes pass)))

(defn derive-pass-default
  ([pass]
   (derive-pass-default nil pass))
  ([salt pass]
   (derive-pass :sha256+hmac512 salt pass)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; expand an already pseudo random key
;;;  if you don't know what you're doing, use the derive-pass function
;;;
(defmulti expand-key (fn [t pass] t) :default :sha128+hmac256)

(defmethod expand-key :sha128+hmac256 [_ k]
  (Key/genHmacSha256 (util/-as-bytes k)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Crypto encoder wrapper

(defn ensure-expanded-key ^Key$ExpandedKey [k]
  (if (instance? Key$ExpandedKey k)
    k
    (gen-expanded-key :sha256+hmac512 k)))

;Create an encoder that encrypt using AES+CBC or AES+GCM.
;           The bits used depends on the key:
;            For AES+CBC+HMAC512 use a of :sha256+hmac512
;            For AES+GCM  use :sha128+hmac256
(defmulti crypto-encoder (fn [t k encoder] t))

(defmethod crypto-encoder :aes-cbc-hmac [_ k encoder]
  (CryptoEncoder/getCBCHmacInstance 0 "SunJCE" ^Key$ExpandedKey (ensure-expanded-key k) encoder))

(defmethod crypto-encoder :aes-gcm [_ k encoder]
  (CryptoEncoder/getGCMInstance ^Key$ExpandedKey (ensure-expanded-key k) encoder))

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
  (KryoEncoder/register ^Class clazz))

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
                [LazySeq (SeqSerde.)]

                [BigInt (BigIntSerde.)]
                ]]
    (doseq[[clazz serde] serdes]
      (KryoEncoder/register ^Class clazz ^Serializer serde))))

;;; register default clojure serializers
(register-serialisers!)
