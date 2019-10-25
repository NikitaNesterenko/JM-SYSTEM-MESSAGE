package jm.controller.rest;

import jm.MessageService;
import jm.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/messages")
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

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<Message>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<Message> messages = messageService.getMessagesByChannelId(id);
        messages.sort(Comparator.comparing(Message::getDateCreate));
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") Long id) {
        return new ResponseEntity<Message>(messageService.getMessageById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateMessage(@RequestBody Message message) {
        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            messageService.updateMessage(message);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
