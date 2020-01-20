package jm.controller.rest;

import jm.DirectMessageService;
import jm.dto.DirectMessageDTO;
import jm.dto.DirectMessageDtoService;
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
    private DirectMessageDtoService directMessageDtoService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService, DirectMessageDtoService directMessageDtoService) {
        this.directMessageService = directMessageService;
        this.directMessageDtoService = directMessageDtoService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DirectMessageDTO> getDirectMessageById(@PathVariable Long id) {
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        logger.info(directMessage.toString());
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<DirectMessageDTO> createDirectMessage(@RequestBody DirectMessageDTO directMessageDTO) {
        DirectMessage directMessage = directMessageDtoService.toEntity(directMessageDTO);
        System.out.println(directMessage);
        directMessageService.saveDirectMessage(directMessage);
        logger.info("Созданное сообщение : {}", directMessage);
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<DirectMessageDTO> updateMessage(@RequestBody DirectMessageDTO messageDTO) {
        DirectMessage message = directMessageDtoService.toEntity(messageDTO);
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<DirectMessageDTO> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/conversation/{id}")
    public ResponseEntity<List<DirectMessageDTO>> getMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(directMessageDtoService.toDto(messages), HttpStatus.OK);
    }
}
