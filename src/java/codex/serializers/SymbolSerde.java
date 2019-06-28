package codex.serializers;

import clojure.lang.Keyword;
import clojure.lang.Symbol;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SymbolSerde extends Serializer<Symbol> {

    @Override
    public void write(Kryo kryo, Output output, Symbol s) {

        output.writeString(s.getNamespace());
        output.writeString(s.getName());
    }

    @Override
    public Symbol read(Kryo kryo, Input input, Class type) {
        return Symbol.intern(input.readString(), input.readString());
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
