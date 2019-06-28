# codex

Simple fast library that compress, encrypt and encode data, for session storage and other use-cases

Wraps over: https://github.com/gerritjvv/encode

[![Clojars Project](https://img.shields.io/clojars/v/com.github.gerritjvv/codex.svg)](https://clojars.org/com.github.gerritjvv/codex)

## Usage

*Default Encoder*

```clojure

(require '[codex.core :as codex])

;; the default encoder uses :aes-cbc-hmac( lz4 (kryo ...)))
(def encoder (codex/default-encoder (codex/expand-pass :sha256+hmac512 "secret")))

(def data {:a 1 :b {:c [1 2 3]}})

(def encrypted-data (codex/encode encoder data))
(def decoded-data (codex/decode encoder encrypted-data))
      
      
;; using records
(defrecord MyRecord [a b])

(codex/register-record! MyRecord)

(def encoder  (codex/default-encoder (codex/expand-pass :sha256+hmac512 "secret")))
(def encrypted-data (codex/encode encoder (->MyRecord 1 2)))
(def decoded-data (codex/decode encoder encrypted-data))


```

*Combining Encoders*
```clojure
(require '[codex.core :as codex])

(def encoder (->>
                  (codex/kryo-encoder)                              ;; convert to bytes
                  (codex/lz4-encoder)                               ;; compress
                  (codex/crypto-encoder                             ;; encrypt
                    :aes-cbc-hmac
                    (codex/expand-pass :sha256+hmac512 "secret"))))

(def data {:a 1 :b {:c [1 2 3]}})

(def encrypted-data (codex/encode encoder data))
(def decoded-data (codex/decode encoder encrypted-data))

```


### Kryo

This library uses [Kryo](https://github.com/EsotericSoftware/kryo) to serialise objects to byte arrays and back.  
Its only pain point is that to be efficient every class and type must be registered.

```clojure

;; Register new records
(codex/register-record!  ...)

;; Bring your own Kryo Serializer
(codex/register-serializer! ...)

```

## More examples:

For Java Examples see:

[Tests](https://github.com/gerritjvv/encode/tree/master/encode-core/src/test/java/encode)


## Performance

See: https://github.com/gerritjvv/encode/tree/master/encode-perf


# License

https://www.apache.org/licenses/LICENSE-2.0

# Contributors

Contributions PRs and suggestions are always welcome.

Please ping me directly in the "issues" on "gerritjvv" or send me an email at gerritjvv@gmail.com, this way
the issues/pull-requests won't just linger if github notifications doens't work.
