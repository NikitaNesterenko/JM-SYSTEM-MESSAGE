package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.DirectMessageService;
import jm.UserService;
import jm.dto.BotDTO;
import jm.dto.DirectMessageDTO;
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

    private final DirectMessageService directMessageService;
    private final UserService userService;

    public DirectMessageRestController(DirectMessageService directMessageService, UserService userService) {
        this.directMessageService = directMessageService;
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get direct message by id",
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
    @Operation(summary = "Create direct message",
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
    @Operation(summary = "Update message",
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
        DirectMessage isCreated = directMessageService.getDirectMessageById(messageDTO.getId());
        if (isCreated == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        message.setDateCreate(isCreated.getDateCreate());
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessageService.getDirectMessageDtoByDirectMessage(directMessage), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: direct message deleted")
            })
    public ResponseEntity<DirectMessageDTO> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/conversation/{id}")
    @Operation(summary = "Get messages by conversation id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = BotDTO.class)
                            ),
                            description = "OK: get direct message by conversation id"
                    )
            })
    public ResponseEntity<List<DirectMessageDTO>> getDirectMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessageDTO> directMessageDtoList = directMessageService.getDirectMessageDtoListByConversationId(id);
        return !directMessageDtoList.isEmpty() ? ResponseEntity.ok(directMessageDtoList) : ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/unread/delete/conversation/{convId}/user/{usrId}")
    public ResponseEntity<?> removeChannelMessageFromUnreadForUser(@PathVariable Long convId, @PathVariable Long usrId) {
        userService.removeDirectMessagesForConversationFromUnreadForUser(convId, usrId);
        return userService.getUserDTOById(usrId).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/unread/conversation/{convId}/user/{usrId}")
    public ResponseEntity<?> getUnreadMessageInChannelForUser(@PathVariable Long convId, @PathVariable Long usrId) {
        List<DirectMessageDTO> directMessageDTOList = directMessageService.getDirectMessageDtoListByUserIdAndConversationId(convId, usrId);
        return ResponseEntity.ok(directMessageDTOList);
    }

    @GetMapping(value = "/unread/add/message/{msgId}/user/{usrId}")
    public ResponseEntity<?> addMessageToUnreadForUser(@PathVariable Long msgId, @PathVariable Long usrId) {
        User user = userService.getUserById(usrId);
        user.getUnreadDirectMessages().add(directMessageService.getDirectMessageById(msgId));
        userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
