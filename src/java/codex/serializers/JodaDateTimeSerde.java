package codex.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.DateTime;


public class JodaDateTimeSerde extends Serializer<DateTime> {

    public static final Registration REGISTRATION = new ComparableRegistration(DateTime.class, new JodaDateTimeSerde(), 3333);
    @Override
    public void write(Kryo kryo, Output output, DateTime b) {
        output.writeLong(b.getMillis());
    }

    @Override
    public DateTime read(Kryo kryo, Input input, Class type) {
          return new DateTime(input.readLong());
    }

    @Override
    public boolean isImmutable() {
        return true;
    }

}
