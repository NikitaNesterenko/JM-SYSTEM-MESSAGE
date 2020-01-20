package jm.controller.rest;

import jm.MessageService;
import jm.dto.MessageDTO;
import jm.dto.MessageDtoService;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/messages")
public class MessageRestController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRestController.class);

    private final MessageService messageService;
    private final MessageDtoService messageDtoService;

    public MessageRestController(MessageService messageService, MessageDtoService messageDtoService) {
        this.messageService = messageService;
        this.messageDtoService = messageDtoService;
    }

    // DTO compliant
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages() {
        logger.info("Список сообщений : ");
        List<Message> messages = messageService.getAllMessages();
        for (Message message : messages) {
            logger.info(message.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(messageDtoService.toDto(messages), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<Message> messages = messageService.getMessagesByChannelId(id);
        messages.sort(Comparator.comparing(Message::getDateCreate));
        logger.info("Полученные сообщения из канала с id = {} :", id);
        for (Message message : messages) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messageDtoService.toDto(messages), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping(value = "/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable("id") Long id) {
        Message message = messageService.getMessageById(id);
        logger.info("Сообщение с id = {}", id);
        logger.info(message.toString());
        return new ResponseEntity<>(messageDtoService.toDto(message), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        List<Message> messages = messageService.getMessagesByChannelIdForPeriod(id, LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        return new ResponseEntity<>(messageDtoService.toDto(messages), HttpStatus.OK);
    }

    // DTO compliant
    @PostMapping(value = "/create")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDto) {
        Message message = messageDtoService.toEntity(messageDto);
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message);
        return new ResponseEntity<>(messageDtoService.toDto(message), HttpStatus.CREATED);
    }

    // DTO compliant
    @PutMapping(value = "/update")
//    @PreAuthorize("#message.user.login == authentication.principal.username")
    public ResponseEntity updateMessage(@RequestBody MessageDTO messageDto, Principal principal) {
        Message message = messageDtoService.toEntity(messageDto);
        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (principal.getName().equals(existingMessage.getUser().getLogin())) {
            logger.info("Существующее сообщение: {}", existingMessage);
            message.setDateCreate(existingMessage.getDateCreate());
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{id}/starred")
    public ResponseEntity<List<MessageDTO>> getStarredMessages(@PathVariable Long id) {
        List<Message> starredMessages = messageService.getStarredMessagesForUser(id);
        logger.info("Сообщения, отмеченные пользователем.");
        return ResponseEntity.ok(messageDtoService.toDto(starredMessages));
    }
}