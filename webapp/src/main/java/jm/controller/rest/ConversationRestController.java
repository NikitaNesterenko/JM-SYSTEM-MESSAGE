package jm.controller.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ConversationService;
import jm.model.Conversation;
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

    public ConversationRestController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @PostMapping(value = "/createOrShow")
    public ResponseEntity<Conversation> createOrShowConversation(@RequestBody Conversation conversation) {
        try {
            conversationService.createOrShowConversation(conversation);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Conversation> updateConversation(@RequestBody Conversation conversation) {
        try {
            Conversation updated = conversationService.updateConversation(conversation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/delete/{conversationID}/{userID}")
    public ResponseEntity<Conversation> deleteConversation(@PathVariable Long conversationID, @PathVariable Long userID) {
        conversationService.deleteConversation(conversationID, userID);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        return ResponseEntity.ok(conversationService.getAllConversations());
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationsByUserId(id));
    }

    @GetMapping(value = "/users/{firstId}/{secondId}")
    public ResponseEntity<Conversation> getConversationByRespondents(@PathVariable Long firstId, @PathVariable Long secondId) {
        return new ResponseEntity<>(
                conversationService.getConversationByUsersId(firstId, secondId),
                HttpStatus.OK
        );
    }
}