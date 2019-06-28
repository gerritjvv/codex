package codex.serializers;

import clojure.lang.AFn;
import clojure.lang.IPersistentList;
import clojure.lang.PersistentList;
import clojure.lang.PersistentVector;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PersistentVectorSerde extends Serializer<PersistentVector> {

    @Override
    public void write(Kryo kryo, Output output, PersistentVector list) {

        int keyLen = list.count();

        output.writeInt(keyLen);

        list.reduce(new AFn() {
            @Override
            public Object invoke(Object state, Object obj) {
                kryo.writeClassAndObject(output, obj);
                return null;
            }
        }, null);

    }

    @Override
    public PersistentVector read(Kryo kryo, Input input, Class type) {

        int keyLen = input.readInt();

        Object[] arr = new Object[keyLen];

        for (int i = 0; i < keyLen; i++) {
            arr[i] = kryo.readClassAndObject(input);
        }

        return PersistentVector.create(arr);
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
