package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.DirectMessageService;
import jm.MessageService;
import jm.UserService;
import jm.dto.BotDTO;
import jm.dto.DirectMessageDTO;
import jm.dto.UserDTO;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/direct_messages")
@Tag(name = "direct message", description = "Direct Message API")
public class DirectMessageRestController {
    private static final Logger logger =
            LoggerFactory.getLogger(DirectMessageRestController.class);

    private final MessageService messageService;
    private final DirectMessageService directMessageService;
    private final UserService userService;

    public DirectMessageRestController(MessageService messageService, DirectMessageService directMessageService, UserService userService) {
        this.messageService = messageService;
        this.directMessageService = directMessageService;
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    @Operation(
            operationId = "getDirectMessageById",
            summary = "Get direct message by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DirectMessageDTO.class)
                            ),
                            description = "OK: get direct message"
                    )
            })
    public ResponseEntity<DirectMessageDTO> getDirectMessageById(@PathVariable Long id) {
        return directMessageService.getDirectMessageDtoByMessageId(id)
                .map(directMessageDTO -> new ResponseEntity<>(directMessageDTO, HttpStatus.OK))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "createDirectMessage",
            summary = "Create direct message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DirectMessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "CREATED: direct message created")
            })
    public ResponseEntity<DirectMessageDTO> createDirectMessage(@RequestBody DirectMessageDTO directMessageDTO) {
        // TODO: ПРОВЕРИТЬ
        //Сохранение личного сообщения выполняется в MessagesController сразу из websocket

        DirectMessage directMessage = directMessageService.getDirectMessageByDirectMessageDto(directMessageDTO);
        directMessage.setDateCreate(LocalDateTime.now());

        directMessageService.saveDirectMessage(directMessage);
        logger.info("Созданное сообщение : {}", directMessage);
        return new ResponseEntity<>(directMessageService.getDirectMessageDtoByDirectMessage(directMessage), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateMessage",
            summary = "Update message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DirectMessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: direct message updated"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to update direct message")
            })
    public ResponseEntity<DirectMessageDTO> updateMessage(@RequestBody DirectMessageDTO messageDTO) {
        // TODO: проверить
        // Обновление личного сообщения выполняется в MessagesController сразу из websocket

        DirectMessage message = directMessageService.getDirectMessageByDirectMessageDto(messageDTO);
        final LocalDateTime dateCreate = messageService.getDateCreateById(messageDTO.getId());
        if (dateCreate == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        message.setDateCreate(dateCreate);
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessageService.getDirectMessageDtoByDirectMessage(directMessage), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            operationId = "deleteMessage",
            summary = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: direct message deleted")
            })
    public ResponseEntity<DirectMessageDTO> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/conversation/{id}")
    @Operation(
            operationId = "getMessagesByConversationId",
            summary = "Get messages by conversation id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = BotDTO.class)
                            ),
                            description = "OK: get direct message by conversation id"
                    )
            })
    public ResponseEntity<List<DirectMessageDTO>> getMessagesByConversationId(@PathVariable Long id) {
        // TODO: ПЕРЕДЕЛАТЬ получать сразу List DirectMessageDto по ConversationId
        return new ResponseEntity<>(
                directMessageService.getDirectMessageDtoListByDirectMessageList(
                        directMessageService.getMessagesByConversationId(id, false)
                ),
                HttpStatus.OK);
    }

    @GetMapping(value = "/unread/delete/conversation/{convId}/user/{usrId}")
    @Operation(
            operationId = "removeChannelMessageFromUnreadForUser",
            summary = "Remove direct messages by conversation id from unread",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: removed direct message by conversation id"
                    )
            })
    public ResponseEntity<?> removeChannelMessageFromUnreadForUser (@PathVariable Long convId, @PathVariable Long usrId) {
        userService.removeDirectMessagesForConversationFromUnreadForUser(convId, usrId);
        return userService.getUserDTOById(usrId).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/unread/conversation/{convId}/user/{usrId}")
    @Operation(
            operationId = "getUnreadMessageInChannelForUser",
            summary = "Get unread messages in channel",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = DirectMessage.class) // need to review
                            ),
                            description = "OK: Got unread messages"
                    )
            })
    public ResponseEntity<?> getUnreadMessageInChannelForUser(@PathVariable Long convId, @PathVariable Long usrId) {
        // TODO: ПЕРЕДЕЛАТЬ получать в дао DirectMessageDto где ConversationId = convId и UserId = usrId

        User user = userService.getUserById(usrId);
        List<DirectMessage> unreadMessages = new ArrayList<>();
        user.getUnreadDirectMessages().forEach(msg -> {
            if (msg.getConversation().getId().equals(convId)) {
                unreadMessages.add(msg);
            }
        });
        return ResponseEntity.ok(directMessageService.getDirectMessageDtoListByDirectMessageList(unreadMessages));
    }

    @GetMapping(value = "/unread/add/message/{msgId}/user/{usrId}")
    @Operation(
            operationId = "addMessageToUnreadForUser",
            summary = "Add direct message to unread",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DirectMessage.class) // need to review
                            ),
                            description = "OK: Got direct message to unread"
                    )
            })
    public ResponseEntity<?> addMessageToUnreadForUser(@PathVariable Long msgId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        DirectMessage directMessage = directMessageService.getDirectMessageById(msgId);
        if ( user == null || directMessage == null ){
            return ResponseEntity.noContent().build();
        }
        user.getUnreadDirectMessages().add(directMessage);
        userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
