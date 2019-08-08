package codex.serializers;

import clojure.lang.BigInt;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JodaDateTimeSerde extends Serializer<DateTime> {

    @Override
    public void write(Kryo kryo, Output output, DateTime b) {


        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(output);
            outputStream.writeLong(b.getMillis());

        } catch (IOException e) {
            RuntimeException rte = new RuntimeException(e);
            rte.setStackTrace(e.getStackTrace());
            throw rte;
        }

    }

    @Override
    public DateTime read(Kryo kryo, Input input, Class type) {
          try {

              ObjectInputStream inputStream = new ObjectInputStream(input);
              long millis = inputStream.readLong();
              return new DateTime(millis);
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
