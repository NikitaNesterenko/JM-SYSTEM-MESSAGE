package jm.controller.rest;

import jm.MessageService;
import jm.model.User;
import jm.model.message.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest/api/messages")
public class MessageRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            MessageRestController.class);

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<ChannelMessage>> getMessages() {
        logger.info("Список сообщений : ");
        for (ChannelMessage message : messageService.getAllMessages()) {
            logger.info(message.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(messageService.getAllMessages(), HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<ChannelMessage>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<ChannelMessage> messages = messageService.getMessagesByChannelId(id);
        messages.sort(Comparator.comparing(ChannelMessage::getDateCreate));
        logger.info("Полученные сообщения из канала с id = {} :", id);
        for (ChannelMessage message : messages) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ChannelMessage> getMessageById(@PathVariable("id") Long id) {
        logger.info("Сообщение с id = {}", id);
        logger.info(messageService.getMessageById(id).toString());
        return new ResponseEntity<>(messageService.getMessageById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    public ResponseEntity<List<ChannelMessage>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return new ResponseEntity<>(messageService.getMessagesByChannelIdForPeriod(id, LocalDateTime.now().minusMonths(3),
                LocalDateTime.now()), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ChannelMessage> createMessage(@RequestBody ChannelMessage message) {
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message + "\n" + message.getId());
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    @PreAuthorize("#message.user.login == authentication.principal.username")
    public ResponseEntity updateMessage(@RequestBody ChannelMessage message, Principal principal) {
        ChannelMessage existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (principal.getName().equals(existingMessage.getUser().getLogin())) {
            logger.info("Существующее сообщение: {}", existingMessage);
            System.out.println(existingMessage.getContent() + " update");
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            System.out.println(message.getContent() + " update");
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

    @GetMapping("/{id}/starred")
    public ResponseEntity<Set<User>> getStarredMessages(@PathVariable Long id) {
        ChannelMessage messageById = messageService.getMessageById(id);
        logger.info("Сообщения, отмеченные пользователем: {}",messageById.getStarredByWhom());
        return ResponseEntity.ok(messageById.getStarredByWhom());
    }
}