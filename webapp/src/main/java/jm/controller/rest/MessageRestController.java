package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MessageService;
import jm.dto.MessageDTO;
import jm.dto.MessageDtoService;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/messages")
@Tag(name = "message", description = "Message API")
public class MessageRestController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRestController.class);

    private final MessageService messageService;
    private final MessageDtoService messageDtoService;

    public MessageRestController(MessageService messageService, MessageDtoService messageDtoService) {
        this.messageService = messageService;
        this.messageDtoService = messageDtoService;
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages() {
        logger.info("Список сообщений : ");
        List<Message> messages = messageService.getAllMessages(false);
        for (Message message : messages) {
            logger.info(message.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(messageDtoService.toDto(messages), HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<Message> messages = messageService.getMessagesByChannelId(id, false);
        messages.sort(Comparator.comparing(Message::getDateCreate));
        logger.info("Полученные сообщения из канала с id = {} :", id);
        for (Message message : messages) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messageDtoService.toDto(messages), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") Long id) {
        Message message = messageService.getMessageById(id);
        logger.info("Сообщение с id = {}", id);
        logger.info(message.toString());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    public ResponseEntity<List<Message>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id,
                                                                            @PathVariable("startDate") String startDate,
                                                                            @PathVariable("endDate") String endDate) {
        List<Message> messages = messageService.getMessagesByChannelIdForPeriod(id, LocalDateTime.now().minusMonths(3), LocalDateTime.now(), false);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
//    @PreAuthorize("#message.user.login == authentication.principal.username")
    public ResponseEntity<?> updateMessage(@RequestBody Message message, Principal principal) {
        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (principal.getName().equals(existingMessage.getUser().getLogin())) {
            logger.info("Существующее сообщение: {}", existingMessage);
            message.setDateCreate(existingMessage.getDateCreate());
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{userId}/{workspaceId}/starred")
    public ResponseEntity<List<MessageDTO>> getStarredMessages(@PathVariable Long userId, @PathVariable Long workspaceId) {
        List<Message> starredMessages = messageService.getStarredMessagesForUserByWorkspaceId(userId, workspaceId, false);
        logger.info("Сообщения, отмеченные пользователем.");
        return ResponseEntity.ok(messageDtoService.toDto(starredMessages));
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Message>> getMessagesFromChannelsForUser(@PathVariable("id") Long userId) {
        logger.info("Список сообщений для юзера от всех @channel: ");
        for (Message message : messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false)) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false), HttpStatus.OK);
    }
}