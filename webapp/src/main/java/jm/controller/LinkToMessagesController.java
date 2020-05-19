package jm.controller;

import jm.MessageService;
import jm.dto.MessageDTO;
import jm.model.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;

@Controller
@RequestMapping("/archives/**")
public class LinkToMessagesController {

    private MessageService messageService;

    public LinkToMessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{base64}/{msgId}")
    @ResponseBody
    public MessageDTO getMessageOfLink(@PathVariable String base64,@PathVariable String msgId){
        if(base64.equals(Base64.getEncoder().encodeToString(msgId.getBytes()))){
            Message message=messageService.getMessageById(Long.parseLong(msgId));
            MessageDTO messageDTO=messageService.getMessageDtoByMessage(message);
            return messageDTO;
        }else{
            return null;
        }
    }
}
