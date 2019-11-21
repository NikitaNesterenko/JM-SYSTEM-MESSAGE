package jm.controller.rest;

import jm.MessageService;
import jm.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    public ResponseEntity<List<Message>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return new ResponseEntity<>(messageService.getMessagesByChannelIdForPeriod(id, startDate, endDate), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Message> updateMessage(@RequestBody Message message) {
        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            existingMessage.setContent(message.getContent());
            existingMessage.setDateCreate(message.getDateCreate());
            messageService.updateMessage(existingMessage);
            return new ResponseEntity<>(existingMessage, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
