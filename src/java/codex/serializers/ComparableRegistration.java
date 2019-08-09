package codex.serializers;

import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;

public class ComparableRegistration extends Registration implements Comparable<Registration>{
    public ComparableRegistration(Class type, Serializer serializer, int id) {
        super(type, serializer, id);
    }

    @Override
    public int compareTo(Registration o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
