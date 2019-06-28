package codex.serializers;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PersistentArrayMapSerde extends Serializer<PersistentArrayMap> {

    @Override
    public void write(Kryo kryo, Output output, PersistentArrayMap map) {
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
    public PersistentArrayMap read(Kryo kryo, Input input, Class type) {
        int keyLen = input.readInt();
        ITransientMap map = PersistentArrayMap.EMPTY.asTransient();

        for (int i = 0; i < keyLen; i++) {
            map = map.assoc(kryo.readClassAndObject(input),
                    kryo.readClassAndObject(input));
        }


        return (PersistentArrayMap)map.persistent();
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
