package jm.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jm.ConversationService;
import jm.model.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/rest/api/conversations")
@Api(value = "Conversation rest",description = "Shows the conversation info")
public class ConversationRestController {

    private ConversationService conversationService;

    @Autowired
    public void setConversationService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @ApiOperation(value = "Return conversation by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/{id}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @ApiOperation(value = "Create conversation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PostMapping(value = "/create")
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        try {
            conversationService.createConversation(conversation);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }

    @ApiOperation(value = "Update conversation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PutMapping(value = "/update")
    public ResponseEntity<Conversation> updateConversation(@RequestBody Conversation conversation) {
        try {
            Conversation updated = conversationService.updateConversation(conversation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation(value = "Delete conversation by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Conversation> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Return conversations")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations(){
        return ResponseEntity.ok(conversationService.gelAllConversations());
    }

    @ApiOperation(value = "Return conversations by user ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationsByUserId(id));
    }

    @ApiOperation(value = "Return conversation by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/users/{firstId}/{secondId}")
    public ResponseEntity<Conversation> getConversationByRespondents(
            @PathVariable Long firstId, @PathVariable Long secondId) {
        return new ResponseEntity<Conversation>(
                conversationService.getConversationByUsers(firstId, secondId),
                HttpStatus.OK
        );

    }

}