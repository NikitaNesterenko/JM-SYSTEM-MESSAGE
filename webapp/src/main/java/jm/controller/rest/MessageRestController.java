package jm.controller.rest;

import jm.MessageService;
import jm.model.Message;
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
    public ResponseEntity<List<Message>> getMessages() {
        logger.info("Список сообщений : ");
        for (Message message: messageService.getAllMessages()) {
            logger.info(message.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(messageService.getAllMessages(), HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<Message>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<Message> messages = messageService.getMessagesByChannelId(id);
        messages.sort(Comparator.comparing(Message::getDateCreate));
        logger.info("Полученные сообщения из канала с id = {} :",id);
        for (Message message: messages) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") Long id) {
        logger.info("Сообщение с id = {}",id);
        logger.info(messageService.getMessageById(id).toString());
        return new ResponseEntity<Message>(messageService.getMessageById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    public ResponseEntity<List<Message>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return new ResponseEntity<>(messageService.getMessagesByChannelIdForPeriod(id,  startDate, endDate), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateMessage(@RequestBody Message message) {
        Message existingMessage = messageService.getMessageById(message.getId());
        logger.info("Существующее сообщение: {}",existingMessage);
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
