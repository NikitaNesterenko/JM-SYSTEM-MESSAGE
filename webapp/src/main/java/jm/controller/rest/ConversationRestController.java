package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Get conversation by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conversation.class)
                            ),
                            description = "OK: get conversation"
                    )
            })
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationById(id));
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create conversation",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conversation.class)
                            )
                    ),
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
    @Operation(summary = "Update conversation",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conversation.class)
                            )
                    ),
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

    @GetMapping(value = "/delete/{conversationID}/{userID}")
    @Operation(summary = "Delete conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: conversation deleted")
            })
    public ResponseEntity<Conversation> deleteConversation(@PathVariable Long conversationID, @PathVariable Long userID) {
        conversationService.deleteConversation(conversationID, userID);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all conversations",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get all conversations"
                    )
            })
    public ResponseEntity<List<Conversation>> getAllConversations() {
        return ResponseEntity.ok(conversationService.gelAllConversations());
    }

    @GetMapping(value = "/user/{id}")
    @Operation(summary = "Get conversation by user id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get conversations"
                    )
            })
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversationsByUserId(id));
    }

    @GetMapping(value = "/users/{firstId}/{secondId}")
    @Operation(summary = "Get conversation by respondents",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get conversation"
                    )
            })
    public ResponseEntity<Conversation> getConversationByRespondents(
            @PathVariable Long firstId, @PathVariable Long secondId) {
        return new ResponseEntity<Conversation>(
                conversationService.getConversationByUsersId(firstId, secondId),
                HttpStatus.OK
        );
    }
}