package jm.controller.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "conversation", description = "Conversation API")
public class ConversationRestController {

    private ConversationService conversationService;

    @Autowired
    public void setConversationService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get conversation by id")
    })
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @PostMapping(value = "/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: conversation created"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to create conversation")
    })
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        try {
            conversationService.createConversation(conversation);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: conversation updated"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to update conversation")
    })
    public ResponseEntity<Conversation> updateConversation(@RequestBody Conversation conversation) {
        try {
            Conversation updated = conversationService.updateConversation(conversation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: conversation deleted")
    })
    public ResponseEntity<Conversation> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get all conversations")
    })
    public ResponseEntity<List<Conversation>> getAllConversations(){
        return ResponseEntity.ok(conversationService.gelAllConversations());
    }

    @GetMapping(value = "/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get conversations by user id")
    })
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationsByUserId(id));
    }

    @GetMapping(value = "/users/{firstId}/{secondId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get conversation by respondents")
    })
    public ResponseEntity<Conversation> getConversationByRespondents(
            @PathVariable Long firstId, @PathVariable Long secondId
    ) {
        return new ResponseEntity<Conversation>(
                conversationService.getConversationByUsers(firstId, secondId),
                HttpStatus.OK
        );
    }

}