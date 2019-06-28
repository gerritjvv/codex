package codex.serializers;

import clojure.lang.BigInt;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BigIntSerde extends Serializer<BigInt> {

    @Override
    public void write(Kryo kryo, Output output, BigInt b) {


        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(output);
            outputStream.writeObject(b);

        } catch (IOException e) {
            RuntimeException rte = new RuntimeException(e);
            rte.setStackTrace(e.getStackTrace());
            throw rte;
        }

    }

    @Override
    public BigInt read(Kryo kryo, Input input, Class type) {
          try {

              ObjectInputStream inputStream = new ObjectInputStream(input);
              return (BigInt) inputStream.readObject();
          } catch (Exception e) {
            RuntimeException rte = new RuntimeException(e);
            rte.setStackTrace(e.getStackTrace());
            throw rte;
        }
    }

    @Override
    public boolean isImmutable() {
        return true;
    }
}
