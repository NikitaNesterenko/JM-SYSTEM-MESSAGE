package jm.controller.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jm.DirectMessageService;
import jm.UserService;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/direct_messages")
@Tag(name = "direct message", description = "Direct Message API")
public class DirectMessageRestController {
    private static final Logger logger = LoggerFactory.getLogger(DirectMessageRestController.class);

    private DirectMessageService directMessageService;
    private UserService userService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService, UserService userService) {
        this.directMessageService = directMessageService;
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DirectMessage> getDirectMessageById(@PathVariable Long id) {
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        return new ResponseEntity<>(directMessage, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<DirectMessage> createDirectMessage(@RequestBody DirectMessage directMessage) {
        directMessageService.saveDirectMessage(directMessage);
        logger.info("Созданное сообщение : {}", directMessage);

        List<User> users = new ArrayList<>();
        users.add(directMessage.getConversation().getAssociatedUser());
        users.add(directMessage.getConversation().getOpeningUser());
        users.forEach(user -> {
            if (user.getOnline().equals(0)) {
                user.getUnreadDirectMessages().add(directMessage);
                userService.updateUser(user);
            }
        });

        return new ResponseEntity<>(directMessage, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<DirectMessage> updateMessage(@RequestBody DirectMessage directMessage) {
        directMessageService.updateDirectMessage(directMessage);
        return new ResponseEntity<>(directMessage, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<DirectMessage> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/conversation/{id}")
    public ResponseEntity<List<DirectMessage>> getMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id, false);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping(value = "/unread/delete/conversation/{convId}/user/{usrId}")
    public ResponseEntity<?> removeChannelMessageFromUnreadForUser (@PathVariable Long convId, @PathVariable Long usrId) {
        userService.removeDirectMessagesForConversationFromUnreadForUser(convId, usrId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/unread/conversation/{convId}/user/{usrId}")
    public ResponseEntity<?> getUnreadMessageInChannelForUser(@PathVariable Long convId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        List<DirectMessage> unreadMessages = new ArrayList<>();
        user.getUnreadDirectMessages().forEach(msg -> {
            if (msg.getConversation().getId().equals(convId)) {
                unreadMessages.add(msg);
            }
        });
        return ResponseEntity.ok(unreadMessages);
    }

    @GetMapping(value = "/unread/add/message/{msgId}/user/{usrId}")
    public ResponseEntity<?> addMessageToUnreadForUser(@PathVariable Long msgId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        user.getUnreadDirectMessages().add(directMessageService.getDirectMessageById(msgId));
        userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
