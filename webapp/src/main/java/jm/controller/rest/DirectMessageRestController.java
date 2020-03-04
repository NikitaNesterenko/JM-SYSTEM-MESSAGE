package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.DirectMessageService;
import jm.dto.BotDTO;
import jm.dto.DirectMessageDTO;
import jm.dto.DirectMessageDtoService;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/direct_messages")
@Tag(name = "direct message", description = "Direct Message API")
public class DirectMessageRestController {
    private static final Logger logger = LoggerFactory.getLogger(DirectMessageRestController.class);

    private DirectMessageService directMessageService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
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
}
