package codex.serializers;

import clojure.lang.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SeqSerde extends Serializer<ISeq> {

    @Override
    public void write(Kryo kryo, Output output, ISeq seq) {

        int count = seq.count();

        output.writeInt(count);
        ISeq seq2 = seq;
        Object first;

        while((first = seq2.first()) != null ) {
            kryo.writeClassAndObject(output, first);
            seq2 = seq2.more();
        }
    }

    @Override
    public ISeq read(Kryo kryo, Input input, Class type) {
        int len = input.readInt();
        Object[] arr = new Object[len];

        for (int i = 0; i < len; i++) {
            arr[i] = kryo.readClassAndObject(input);
        }


        return PersistentVector.create(arr).seq();
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
