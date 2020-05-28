package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ConversationService;
import jm.component.Response;
import jm.dto.ConversationDTO;
import jm.model.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/rest/api/conversations")
@Tag(name = "conversation", description = "Conversation API")
public class ConversationRestController {
    private static final Logger logger =
            LoggerFactory.getLogger(ConversationRestController.class);
    private ConversationService conversationService;

    public ConversationRestController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping(value = "/{id}")
    @Operation(
            operationId = "getConversationById",
            summary = "Get conversation by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conversation.class)
                            ),
                            description = "OK: get conversation"
                    )
            })
    public Response<Conversation> getConversationById(@PathVariable Long id) {
        try {
            return Response.ok(conversationService.getConversationById(id));
        } catch (Exception e) {
            return Response.error(HttpStatus.BAD_REQUEST,"error get conversation");
        }
    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "createConversation",
            summary = "Create conversation",
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
    public Response<Conversation> createConversation(@RequestBody ConversationDTO conversationDTO) {
        Conversation conversation = conversationService.getEntityFromDTO(conversationDTO);
        try {
            conversationService.createConversation(conversation);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            Response.error(HttpStatus.BAD_REQUEST,"error to create conversation");
        }
        return Response.ok(conversation);
    }

    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateConversation",
            summary = "Update conversation",
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
    public Response<Conversation> updateConversation(@RequestBody Conversation conversation) {
        try {
            Conversation updated = conversationService.updateConversation(conversation);
            return Response.ok(updated);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return Response.error(HttpStatus.BAD_REQUEST,"error to uprdate conversation");
        }
    }

    @GetMapping(value = "/delete/{conversationID}/{userID}")
    @Operation(
            operationId = "deleteConversation",
            summary = "Delete conversation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: conversation deleted")
            })
    public Response<Conversation> deleteConversation(@PathVariable Long conversationID, @PathVariable Long userID) {
        conversationService.deleteConversation(conversationID, userID);
        return Response.ok().build();
    }

    @GetMapping
    @Operation(
            operationId = "getAllConversations",
            summary = "Get all conversations",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get all conversations"
                    )
            })
    public Response<List<Conversation>> getAllConversations() {
        List<Conversation> conversationsList = conversationService.getAllConversations();
        return conversationsList.isEmpty() ? Response.error(HttpStatus.BAD_REQUEST,"error to get conversation list") : Response.ok(conversationsList);
    }

    @GetMapping(value = "/user/{id}")
    @Operation(
            operationId = "getConversationsByUserId",
            summary = "Get conversation by user id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get conversations"
                    )
            })
    public Response<List<Conversation>> getConversationsByUserId(@PathVariable Long id) {
        List<Conversation> conversationList = conversationService.getConversationsByUserId(id);
        return conversationList.isEmpty() ? Response.error(HttpStatus.BAD_REQUEST,"error to get conversation by user id") : Response.ok(conversationList);
    }

    @GetMapping(value = "/users/{firstId}/{secondId}")
    @Operation(
            operationId = "getConversationByRespondents",
            summary = "Get conversation by respondents",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Conversation.class)
                            ),
                            description = "OK: get conversation"
                    )
            })
    public Response<Conversation> getConversationByRespondents(
            @PathVariable Long firstId, @PathVariable Long secondId) {
        try {
            return Response.ok(
                    conversationService.getConversationByUsersId(firstId, secondId)
            );
        } catch (Exception e) {
            return Response.error(HttpStatus.BAD_REQUEST,"error to get conversation by respondents");
        }
    }
}