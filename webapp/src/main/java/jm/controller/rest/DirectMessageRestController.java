package jm.controller.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "direct message", description = "Direct Message API")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get direct message by id")
    })
    public ResponseEntity<DirectMessageDTO> getDirectMessageById(@PathVariable Long id) {
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        logger.info(directMessage.toString());
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED: direct message created")
    })
    public ResponseEntity<DirectMessageDTO> createDirectMessage(@RequestBody DirectMessageDTO directMessageDTO) {
        DirectMessage directMessage = directMessageDtoService.toEntity(directMessageDTO);
        System.out.println(directMessage);
        directMessageService.saveDirectMessage(directMessage);
        logger.info("Созданное сообщение : {}", directMessage);
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: direct message updated"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to update direct message")
    })
    public ResponseEntity<DirectMessageDTO> updateMessage(@RequestBody DirectMessageDTO messageDTO) {
        DirectMessage message = directMessageDtoService.toEntity(messageDTO);
        DirectMessage isCreated = directMessageService.getDirectMessageById(messageDTO.getId());
        if (isCreated == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        message.setDateCreate(isCreated.getDateCreate());
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: direct message deleted")
    })
    public ResponseEntity<DirectMessageDTO> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/conversation/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get direct message by conversation id")
    })
    public ResponseEntity<List<DirectMessageDTO>> getMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(directMessageDtoService.toDto(messages), HttpStatus.OK);
    }
}
