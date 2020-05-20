package jm.controller;

import jm.MessageService;
import jm.dto.MessageDTO;
import jm.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/archives/**")
public class LinkToMessagesController {

    private MessageService messageService;

    public LinkToMessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{base64}/{msgId}")
    @ResponseBody
    public ResponseEntity<Long> getMessageOfLink(@PathVariable String base64, @PathVariable String msgId) {
        Long id = null;
        if (base64.equals(Base64.getEncoder().encodeToString(msgId.getBytes())) &&
                messageService.getMessageDtoById(Long.parseLong(msgId)) != null) {
            id = Long.parseLong(msgId);
            return ResponseEntity.ok().body(id);
        }
        return ResponseEntity.badRequest().build();
    }
}
