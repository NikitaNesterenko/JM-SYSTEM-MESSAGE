package jm.controller.rest;

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
public class ConversationRestController {

    private ConversationService conversationService;

    @Autowired
    public void setConversationService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        try {
            conversationService.createConversation(conversation);
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

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Conversation> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations(){
        return ResponseEntity.ok(conversationService.gelAllConversations());
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationsByUserId(id));
    }

}