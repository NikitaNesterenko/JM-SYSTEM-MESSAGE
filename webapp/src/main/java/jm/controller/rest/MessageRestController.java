package jm.controller.rest;

import jm.MessageService;
import jm.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

   private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages() {
        return new ResponseEntity<>(messageService.getAllMessages(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") Long id) {
        return new ResponseEntity<Message>(messageService.getMessageById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateMessage(@RequestBody Message message) {
        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            messageService.updateMessage(message);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
