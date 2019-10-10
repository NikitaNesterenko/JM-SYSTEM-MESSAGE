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
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity updateMessage(@RequestBody Message message) {
        messageService.updateMessage(message);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteMessage(@RequestBody Message message) {
        messageService.deleteMessage(message);
        return new ResponseEntity(HttpStatus.OK);
    }
}
