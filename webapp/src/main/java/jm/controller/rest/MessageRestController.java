package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.MessageService;
import jm.UserService;
import jm.dto.MessageDTO;
import jm.dto.MessageDtoService;
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
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/api/messages")
@Tag(name = "message", description = "Message API")
public class MessageRestController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRestController.class);

    private final MessageService messageService;
    private final MessageDtoService messageDtoService;
    private final ChannelService channelService;
    private final UserService userService;

    public MessageRestController(MessageService messageService, MessageDtoService messageDtoService, ChannelService channelService, UserService userService) {
        this.messageService = messageService;
        this.messageDtoService = messageDtoService;
        this.channelService = channelService;
        this.userService = userService;
    }

    // DTO compliant
    @GetMapping
    @Operation(summary = "Get all messages",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public ResponseEntity<List<MessageDTO>> getMessages () {
        List<MessageDTO> messageDTOList = messageService.getAllMessageDtoByIsDeleted(false);
        if (!messageDTOList.isEmpty()) {
            logger.info("Список сообщений : ");
            messageDTOList.forEach(messageDTO -> logger.info(messageDTO.toString()));
            logger.info("-----------------------");
            return new ResponseEntity<>(messageDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest()
                           .build();
        }
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    @Operation(summary = "Get messages by channel id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public ResponseEntity<List<MessageDTO>> getMessagesByChannelId (@PathVariable("id") Long id) {
        List<MessageDTO> messageDTOList = messageService.getMessageDtoListByChannelId(id, false);
        if (!messageDTOList.isEmpty()) {
            logger.info("Полученные сообщения из канала с id = {} :", id);
            messageDTOList.forEach(messageDTO -> logger.info(messageDTO.toString()));
            return new ResponseEntity<>(messageDTOList, HttpStatus.OK);
        } else {
            logger.info("Сообщения из канала с id = {} не получены", id);
            return ResponseEntity.badRequest()
                           .build();
        }
    }

    // DTO compliant
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get message by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDTO.class)
                            ),
                            description = "OK: get message"
                    )
            })
    public ResponseEntity<MessageDTO> getMessageById (@PathVariable("id") Long id) {
        return messageService.getMessageDtoById(id)
                       .map(messageDTO -> new ResponseEntity<>(messageDTO, HttpStatus.OK))
                       .orElse(ResponseEntity.badRequest()
                                       .build());
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}/{startDate}/{endDate}")
    @Operation(summary = "Get messages by channel & period",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get messages"
                    )
            })
    public ResponseEntity<List<MessageDTO>> getMessagesByChannelIdForPeriod (@PathVariable("id") Long id,
                                                                             @PathVariable("startDate") String startDate,
                                                                             @PathVariable("endDate") String endDate) {
        //TODO: DELETE

        System.out.println("CHECKING");
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        List<MessageDTO> messageDTOList = messageService.getMessagesDtoByChannelIdForPeriod(id, LocalDateTime.now().minusMonths(3), LocalDateTime.now(), false);
//        List<MessageDTO> messageDTOList = messageService.getMessagesDtoByChannelIdForPeriod(id, startDate, endDate, false);

        if (!messageDTOList.isEmpty()) {
            return new ResponseEntity<>(messageDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest()
                           .build();
        }
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "CREATED: message created")
            })
    public ResponseEntity<MessageDTO> createMessage (@RequestBody MessageDTO messageDto) {
        // TODO: ПРОВЕРИТЬ
        // Сохранение сообщения выполняется в MessagesController сразу из websocket

        Message message = messageService.getMessageByMessageDTO(messageDto);
        message.setDateCreate(LocalDateTime.now());
        messageService.createMessage(message);
        logger.info("Созданное сообщение : {}", message);
        MessageDTO messageDTO = messageService.getMessageDtoByMessage(message);
        return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
    }

    // DTO compliant
    @PutMapping(value = "/update")
    @Operation(summary = "Update message",
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
    public ResponseEntity updateMessage (@RequestBody MessageDTO messageDto, Principal principal) {
        // TODO: проверить
        // Обновление сообщения выполняется в MessagesController сразу из websocket

        Message message = messageService.getMessageByMessageDTO(messageDto);

        Message existingMessage = messageService.getMessageById(message.getId());
        if (existingMessage == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (principal.getName()
                    .equals(existingMessage.getUser()
                                    .getLogin())) {
            logger.info("Существующее сообщение: {}", existingMessage);
            message.setDateCreate(existingMessage.getDateCreate());
            messageService.updateMessage(message);
            logger.info("Обновленное сообщение: {}", message);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: message deleted")
            })
    public ResponseEntity deleteMessage (@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{userId}/{workspaceId}/starred")
    @Operation(summary = "Get starred messages",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MessageDTO.class)
                            ),
                            description = "OK: get stared messages"
                    )
            })
    public ResponseEntity<List<MessageDTO>> getStarredMessages (@PathVariable Long userId, @PathVariable Long workspaceId) {
        List<MessageDTO> messageDTOS = messageService.getStarredMessagesDTOForUserByWorkspaceId(userId, workspaceId, false);
        if (!messageDTOS.isEmpty()) {
            logger.info("Сообщения, отмеченные пользователем.");
            return ResponseEntity.ok(messageDTOS);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Message>> getMessagesFromChannelsForUser (@PathVariable("id") Long userId) {
        logger.info("Список сообщений для юзера от всех @channel: ");
        for (Message message : messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false)) {
            logger.info(message.toString());
        }
        return new ResponseEntity<>(messageService.getAllMessagesReceivedFromChannelsByUserId(userId, false), HttpStatus.OK);
    }

    @GetMapping(value = "/unread/delete/channel/{chnId}/user/{usrId}")
    public ResponseEntity<?> removeChannelMessageFromUnreadForUser(@PathVariable Long chnId, @PathVariable Long usrId) {
        userService.removeChannelMessageFromUnreadForUser(chnId, usrId);
        return userService.getUserDTOById(usrId).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/unread/channel/{chnId}/user/{usrId}")
    public ResponseEntity<?> getUnreadMessageInChannelForUser(@PathVariable Long chnId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        List<Message> unreadMessages = new ArrayList<>();
        user.getUnreadMessages().forEach(msg -> {
            if (msg.getChannelId().equals(chnId)) {
                unreadMessages.add(msg);
            }
        });
        return ResponseEntity.ok(messageDtoService.toDto(unreadMessages));
    }

    @GetMapping(value = "/unread/add/message/{msgId}/user/{usrId}")
    public ResponseEntity<?> addMessageToUnreadForUser(@PathVariable Long msgId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        user.getUnreadMessages().add(messageService.getMessageById(msgId));
        userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}