package jm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.model.Message;
import jm.model.message.DirectMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessagesController {

    //TODO кто придумал эту тупость и создал отдельные классы для каждого метода стомп клиента DELETE THIS MESSAGE
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String messageCreation(Message message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }

    @MessageMapping("/thread")
    @SendTo("/topic/threads")
    public String threadCreation(Message message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }

    @MessageMapping("/direct_message")
    @SendTo("/topic/dm")
    public String directMessageCreation(DirectMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }
}
