package codex.serializers;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class PersistentHashSetSerde extends Serializer<PersistentHashSet> {

    @Override
    public void write(Kryo kryo, Output output, PersistentHashSet set) {

        int keyLen = set.count();

        output.writeInt(keyLen);

        Iterator it = set.iterator();

        while(it.hasNext()){
            kryo.writeClassAndObject(output, it.next());
        }
    }

    @Override
    public PersistentHashSet read(Kryo kryo, Input input, Class type) {

        int keyLen = input.readInt();
        Object[] arr = new Object[keyLen];

        for (int i = 0; i < keyLen; i++) {
            arr[i] = kryo.readClassAndObject(input);
        }


        return PersistentHashSet.create(arr);
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
