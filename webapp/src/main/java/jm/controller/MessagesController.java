package jm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.model.InputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessagesController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String messageCreation(InputMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
        
    }
}
