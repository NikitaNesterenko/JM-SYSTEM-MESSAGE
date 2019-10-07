package jm.controller;

import jm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageRestController {

    private UserService userService;
//    private ChannelService channelService;
    private MessageService messageService;

    @Autowired
    public MessageRestController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping(value = "/rest/messages/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message result = messageService.getMessageById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/rest/messages")
    public ResponseEntity<List<Message>> getAllMessagesByChannel(@RequestParam Channel channel) {
        Channel foundedChannel = new Channel(); // need ChannelService !!! WRONG
        List<Message> result = messageService.getAllMessagesByChannel(foundedChannel);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/rest/messages")
    public ResponseEntity<List<Message>> getAllMessagesByUser(@RequestParam User user) {
        User foundedUser = userService.getUserById(user.getId());
        List<Message> result = messageService.getAllMessagesByUser(foundedUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/rest/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message == null || message.getUser() == null || message.getChannel() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User foundedUser = userService.getUserById(message.getUser().getId());
        Channel foundedChannel = new Channel(); // need ChannelService !!! WRONG
        message.setUser(foundedUser);
        message.setChannel(foundedChannel);
        messageService.createMessage(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/rest/messages")
    public ResponseEntity<Message> updateMessage(@RequestBody Message message) {
        messageService.updateMessage(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/rest/messages/{id}")
    public ResponseEntity<Message> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(messageService.getMessageById(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
