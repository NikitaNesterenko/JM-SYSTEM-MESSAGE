package jm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.dto.MessageDTO;
import jm.dto.ThreadMessageDTO;
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

    @MessageMapping("/thread")
    @SendTo("/topic/threads")
    public String threadCreation(ThreadMessageDTO message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }
}
