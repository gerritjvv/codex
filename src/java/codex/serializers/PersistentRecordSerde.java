package codex.serializers;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Map;

public class PersistentRecordSerde extends Serializer<Map> {


    final IFn recordConstructor;

    public PersistentRecordSerde(IFn recordConstructor) {
        this.recordConstructor = recordConstructor;
    }

    @Override
    public void write(Kryo kryo, Output output, Map map) {

        int keyLen = map.size();
        output.writeInt(keyLen);

        map.forEach((k, v)-> {
                             kryo.writeClassAndObject(output, k);
                             kryo.writeClassAndObject(output, v);
                     });
    }

    @Override
    public Map read(Kryo kryo, Input input, Class type) {

        int keyLen = input.readInt();
        ITransientMap map = PersistentArrayMap.EMPTY.asTransient();

        for (int i = 0; i < keyLen; i++) {
            map = map.assoc(kryo.readClassAndObject(input),
                    kryo.readClassAndObject(input));
        }


        return (Map)recordConstructor.invoke(map.persistent());
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
