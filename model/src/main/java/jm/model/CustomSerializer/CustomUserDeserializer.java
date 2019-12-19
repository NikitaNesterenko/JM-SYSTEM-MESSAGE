//package jm.model.CustomSerializer;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import jm.api.dao.MessageDAO;
//import jm.model.message.ChannelMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Set;
//
//@Service
//public class CustomUserDeserializer extends StdDeserializer<Set<ChannelMessage>> {
//    MessageDAO messageDAO;
//
//    @Autowired
//    public void setMessageDAO(MessageDAO messageDAO){
//        this.messageDAO = messageDAO;
//    }
//
//    public CustomUserDeserializer() {
//        this(null);
//    }
//
//    public CustomUserDeserializer(Class<?> vc) {
//        super(vc);
//    }
//
//    @Override
//    public Set<ChannelMessage> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//        System.out.println("*****************************************");
//        JsonNode tree = p.getCodec().readTree(p);
//
//        for (JsonNode node : tree) {
//            System.out.println(node.asLong());
//            Long msgId = node.asLong();
//            ChannelMessage msg = messageDAO.getById(msgId);
//            System.out.println(msg);
//        }
//
////        System.out.println(node);
//        System.out.println(ctxt);
//
//        return null;
//    }
//}
