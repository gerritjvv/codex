(ns codex.util)


(defprotocol Bytes
  (-as-bytes [this] "Return the byte array representation"))


(extend-protocol Bytes
  String
  (-as-bytes [v] (.getBytes (str v) "UTF-8")))

(extend-protocol Bytes
  nil
  (-as-bytes [_] nil))


(extend-protocol Bytes
  (Class/forName "[B")
  (-as-bytes [v]
    v))
