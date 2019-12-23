package jm.controller.rest;

import jm.DirectMessageService;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/direct_messages")
public class DirectMessageRestController {
    private static final Logger logger =
            LoggerFactory.getLogger(DirectMessageRestController.class);

    private DirectMessageService directMessageService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DirectMessage> getDirectMessageById(@PathVariable Long id) {
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        logger.info(directMessage.toString());
        return new ResponseEntity<DirectMessage>(directMessage, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<DirectMessage> createDirectMessage(@RequestBody DirectMessage message) {
        directMessageService.saveDirectMessage(message);
        logger.info("Созданное сообщение : {}", message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<DirectMessage> updateMessage(@RequestBody DirectMessage message) {
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
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
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
