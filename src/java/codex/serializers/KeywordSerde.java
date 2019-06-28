package codex.serializers;

import clojure.lang.AFn;
import clojure.lang.ITransientMap;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KeywordSerde extends Serializer<Keyword> {

    @Override
    public void write(Kryo kryo, Output output, Keyword kw) {

        output.writeString(kw.getNamespace());
        output.writeString(kw.getName());
    }

    @Override
    public Keyword read(Kryo kryo, Input input, Class type) {
        return Keyword.intern(input.readString(), input.readString());
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
