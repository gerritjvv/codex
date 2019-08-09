# codex

Simple fast library that compress, encrypt and encode data, for session storage and other use-cases

Wraps over: https://github.com/gerritjvv/encode

[![Clojars Project](https://img.shields.io/clojars/v/com.github.gerritjvv/codex.svg)](https://clojars.org/com.github.gerritjvv/codex)

## Usage

**Important**

Always call `(codex/register-serialisers!)` once to ensure all the clojure serializers are added. 

*Default Encoder*

```clojure

(require '[codex.core :as codex])

(codex/register-serialisers!)

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

(codex/register-serialisers!)

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

### Important!! please read.

The Kryo library uses integers to identity types and serializers when reading a message.  
When you do not specify this integer (as with the functions above), the order is important.  
This is not an issue if the messages you store are not long lived. If they are, like disk persistent  
you must use the `Registration` class and hard code a "id" integer to avoid situations where if you
change the order (this is easy to get wrong) your messages will not be readable.  

See:
```
register-registration
;; and
serializers.JodaDateTimeSerde 
```

for examples.


## More examples:

For More Examples see:

[Tests](https://github.com/gerritjvv/codex/tree/master/test/codex)


## Performance

See: https://github.com/gerritjvv/encode/tree/master/encode-perf


# License

https://www.apache.org/licenses/LICENSE-2.0

# Contributors

Contributions PRs and suggestions are always welcome.

Please ping me directly in the "issues" on "gerritjvv" or send me an email at gerritjvv@gmail.com, this way
the issues/pull-requests won't just linger if github notifications doens't work.
