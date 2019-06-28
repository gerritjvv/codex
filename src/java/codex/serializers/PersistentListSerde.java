package codex.serializers;

import clojure.lang.IPersistentList;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentList;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;
import java.util.ListIterator;

public class PersistentListSerde extends Serializer<PersistentList> {

    @Override
    public void write(Kryo kryo, Output output, PersistentList list) {

        int keyLen = list.count();

        output.writeInt(keyLen);

        for(int i = 0; i < keyLen; i++) {
            kryo.writeClassAndObject(output, list.get(i));
        }
    }

    @Override
    public PersistentList read(Kryo kryo, Input input, Class type) {

        int keyLen = input.readInt();

        IPersistentList list = PersistentList.EMPTY;

        for (int i = 0; i < keyLen; i++) {
            list = (IPersistentList)list.cons(kryo.readClassAndObject(input));
        }


        return (PersistentList) list;
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
