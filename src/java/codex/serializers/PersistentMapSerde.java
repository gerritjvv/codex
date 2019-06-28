package codex.serializers;

import clojure.lang.AFn;
import clojure.lang.ITransientMap;
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentHashMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Map;

public class PersistentMapSerde extends Serializer<PersistentHashMap> {

    @Override
    public void write(Kryo kryo, Output output, PersistentHashMap map) {

        int keyLen = map.count();
        output.writeInt(keyLen);

        map.kvreduce(new AFn() {
                         @Override
                         public Object invoke(Object state, Object k, Object v) {
                             kryo.writeClassAndObject(output, k);
                             kryo.writeClassAndObject(output, v);
                             return null;
                         }
                     },
                null);
    }

    @Override
    public PersistentHashMap read(Kryo kryo, Input input, Class type) {

        int keyLen = input.readInt();
        ITransientMap map = PersistentArrayMap.EMPTY.asTransient();

        for (int i = 0; i < keyLen; i++) {
            map = map.assoc(kryo.readClassAndObject(input),
                    kryo.readClassAndObject(input));
        }


        return (PersistentHashMap)map.persistent();
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
