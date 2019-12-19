package jm.model.CustomSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jm.model.message.ChannelMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CustomUserSerializer extends StdSerializer<Set<ChannelMessage>> {

    public CustomUserSerializer() {
        this(null);
    }

    public CustomUserSerializer(Class<Set<ChannelMessage>> t) {
        super(t);
    }

    @Override
    public void serialize(Set<ChannelMessage> messages, JsonGenerator gen, SerializerProvider provider) throws IOException {

        Set<Long> tempUser = new HashSet<>();
        for (ChannelMessage u : messages) {
            tempUser.add(u.getId());
        }
        gen.writeObject(tempUser);
    }
}
