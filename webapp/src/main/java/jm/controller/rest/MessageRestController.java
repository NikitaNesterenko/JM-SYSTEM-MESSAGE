package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.MessageService;
import jm.UserService;
import jm.component.Response;
import jm.dto.MessageDTO;
import jm.dto.UserDTO;
import jm.model.Message;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/api/messages")
@Tag(name = "message", description = "Message API")
public class MessageRestController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRestController.class);

    private final MessageService messageService;
    private final ChannelService channelService;
    private final UserService userService;

    public MessageRestController(MessageService messageService, ChannelService channelService, UserService userService) {
        this.messageService = messageService;
        this.channelService = channelService;
        this.userService = userService;
    }

    // DTO compliant
    @GetMapping
    @Operation(
            operationId = "getMessages",
            summary = "Get all messages",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public Response<List<MessageDTO>> getMessages() {
        List<MessageDTO> messageDTOList = messageService.getAllMessageDtoByIsDeleted(false);
        return messageDTOList.isEmpty() ? Response.error(HttpStatus.NO_CONTENT,"entity Message не обнаружен") : Response.ok(messageDTOList);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    //если в канале сообщений нет то идет ошибка 400 и отображается с ошибкой. если есть сообщение в базе то ошибок нет
    @Operation(
            operationId = "getMessagesByChannelId",
            summary = "Get messages by channel id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public Response<List<MessageDTO>> getMessagesByChannelId(@PathVariable("id") Long id) {
        List<MessageDTO> messageDTOList = messageService.getMessageDtoListByChannelId(id, false);
        return messageDTOList.isEmpty() ? Response.error(HttpStatus.BAD_REQUEST,"entity Message не обнаружен") : Response.ok(messageDTOList);
    }

    // DTO compliant
    @GetMapping(value = "/{id}")
    @Operation(
            operationId = "getMessageById",
            summary = "Get message by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDTO.class)
                            ),
                            description = "OK: get message"
                    )
            })
    public Response<MessageDTO> getMessageById(@PathVariable("id") Long id) {
        return messageService.getMessageDtoById(id)
                .map(messageDTO -> Response.ok(messageDTO))
                .orElse(Response.error(HttpStatus.BAD_REQUEST,"entity Message не обнаружен"));
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    @Operation(
            operationId = "getMessagesByChannelIdForPeriod",
            summary = "Get messages by channel & period",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public Response<List<MessageDTO>> getMessagesByChannelIdForPeriod(@PathVariable("id") Long id,
                                                                            @PathVariable("startDate") String startDate,
                                                                            @PathVariable("endDate") String endDate) {
        List<MessageDTO> messageDTOList = messageService.getMessagesDtoByChannelIdForPeriod(id, LocalDateTime.now()

                .minusMonths(3), LocalDateTime.now(), false);

        return Response.ok(messageDTOList);

    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "createMessage",
            summary = "Create message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "CREATED: message created")
            })
    public Response<MessageDTO> createMessage(@RequestBody MessageDTO messageDto) {
        // TODO: ПРОВЕРИТЬ
        // Сохранение сообщения выполняется в MessagesController сразу из websocket

        Message message = messageService.getMessageByMessageDTO(messageDto);
        message.setDateCreate(LocalDateTime.now());
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message);
        MessageDTO messageDTO = messageService.getMessageDtoByMessage(message);
        return Response.ok(messageDTO);
    }

    // DTO compliant
    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateMessage",
            summary = "Update message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: message updated"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN: unable to update message"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to find message")
            })
    public Response updateMessage(@RequestBody MessageDTO messageDto, Principal principal) {
        // TODO: проверить
        // Обновление сообщения выполняется в MessagesController сразу из websocket

        Message message = messageService.getMessageByMessageDTO(messageDto);

        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return Response.error(HttpStatus.BAD_REQUEST,"entity Message не обнаружен");
        }
        if (principal.getName()
                .equals(existingMessage.getUser()
                        .getLogin())) {
            logger.info("Существующее сообщение: {}", existingMessage);
            message.setDateCreate(existingMessage.getDateCreate());
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            return Response.ok().build();
        }
        return Response.error(HttpStatus.BAD_REQUEST,"error to update Message");
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            operationId = "deleteMessage",
            summary = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: message deleted")
            })
    public Response deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return Response.ok().build();
    }

    // DTO compliant
    @GetMapping("/{userId}/{workspaceId}/starred")
    @Operation(
            operationId = "getStarredMessages",
            summary = "Get starred messages",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get stared messages"
                    )
            })
    public Response<List<MessageDTO>> getStarredMessages(@PathVariable Long userId, @PathVariable Long workspaceId) {
        List<MessageDTO> messageDTOS = messageService.getStarredMessagesDTOForUserByWorkspaceId(userId, workspaceId, false);
        return !messageDTOS.isEmpty() ?
                Response.ok(messageDTOS) : Response.error(HttpStatus.BAD_REQUEST,"entity Message не обнаружен");
    }

    @GetMapping(value = "/user/{id}")
    @Operation(
            operationId = "getMessagesFromChannelsForUser",
            summary = "Get list of messages by userId",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: Got all message from all channels"
                    )
            })
    public Response<List<Message>> getMessagesFromChannelsForUser(@PathVariable("id") Long userId) {
        logger.info("Список сообщений для юзера от всех @channel: ");
        for (Message message : messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false)) {
            logger.info(message.toString());
        }
        return Response.ok(messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false));
    }

    @GetMapping(value = "/unread/delete/channel/{chnId}/user/{usrId}")
    @Operation(
            operationId = "removeChannelMessageFromUnreadForUser",
            summary = "Remove message from unread",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: Removed message from unread"
                    )
            })
    public Response<?> removeChannelMessageFromUnreadForUser(@PathVariable Long chnId, @PathVariable Long usrId) {
        userService.removeChannelMessageFromUnreadForUser(chnId, usrId);
        return userService.getUserDTOById(usrId).map(Response::ok)
                .orElseGet(() -> Response.error(HttpStatus.BAD_REQUEST,"error to remove message from unread"));
    }

    @GetMapping(value = "/unread/channel/{chnId}/user/{usrId}")
    @Operation(
            operationId = "getUnreadMessageInChannelForUser",
            summary = "Get unread messages",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: Got all unread messages"
                    )
            })
    public Response<?> getUnreadMessageInChannelForUser(@PathVariable Long chnId, @PathVariable Long usrId) {
        // TODO: ПЕРЕДЕЛАТЬ получать в дао MessageDtoLis где UserId = usrId и ChannelId = chnId

        User user = userService.getUserById(usrId);
        List<Message> unreadMessages = new ArrayList<>();
        user.getUnreadMessages().forEach(msg -> {
            if (msg.getChannelId().equals(chnId)) {
                unreadMessages.add(msg);
            }
        });
        return Response.ok(messageService.getMessageDtoListByMessageList(unreadMessages));
    }

    @GetMapping(value = "/unread/add/message/{msgId}/user/{usrId}")
    @Operation(
            operationId = "addMessageToUnreadForUser",
            summary = "Add unread message",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Message.class)
                            ),
                            description = "OK: Got message to unread"
                    )
            })
    public Response<?> addMessageToUnreadForUser(@PathVariable Long msgId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        user.getUnreadMessages().add(messageService.getMessageById(msgId));
        userService.updateUser(user);
        return Response.ok().build();
    }

    @GetMapping(value = "/associated/{id}")
    @Operation(
            operationId = "getMessagesFromAssociatedWithUser",
            summary = "Get list of messages by userId",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: Got all message from all channels"
                    )
            })
    public ResponseEntity<List<Message>> getMessagesFromAssociatedWithUser(@PathVariable("id") Long userId) {
        logger.info("Список сообщений для юзера где его упомянули: ");
        messageService.getAllMessagesAssociatedWithUser(userId, false)
                .forEach(message -> logger.info(message.toString()));
        return ResponseEntity.ok(messageService.getAllMessagesAssociatedWithUser(userId, false));
    }
}