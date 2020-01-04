package jm.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Direct Message rest",description = "Shows the Direct Message info")
public class DirectMessageRestController {
    private static final Logger logger =
            LoggerFactory.getLogger(DirectMessageRestController.class);

    private DirectMessageService directMessageService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
    }

    @ApiOperation(value = "Returns direct message by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/{id}")
    public ResponseEntity<DirectMessage> getDirectMessageById(@PathVariable Long id) {
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        logger.info(directMessage.toString());
        return new ResponseEntity<DirectMessage>(directMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "Create direct message")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PostMapping(value = "/create")
    public ResponseEntity<DirectMessage> createDirectMessage(@RequestBody DirectMessage message) {
        directMessageService.saveDirectMessage(message);
        logger.info("Созданное сообщение : {}", message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update direct message")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PutMapping(value = "/update")
    public ResponseEntity<DirectMessage> updateMessage(@RequestBody DirectMessage message) {
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete direct message by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<DirectMessage> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Returns direct messages by conversation ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/conversation/{id}")
    public ResponseEntity<List<DirectMessage>> getMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
