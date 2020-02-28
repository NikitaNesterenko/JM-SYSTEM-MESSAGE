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
import jm.dto.DirectMessageDtoService;
import jm.dto.UserDtoService;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private DirectMessageService directMessageService;
    private DirectMessageDtoService directMessageDtoService;
    private UserService userService;
    private UserDtoService userDtoService;

    @Autowired
    public void setDirectMessageService(DirectMessageService directMessageService, DirectMessageDtoService directMessageDtoService,
                                        UserService userService, UserDtoService userDtoService) {
        this.directMessageService = directMessageService;
        this.directMessageDtoService = directMessageDtoService;
        this.userService = userService;
        this.userDtoService = userDtoService;
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
        logger.info("Сообщение с id = {}", id);
        DirectMessage directMessage = directMessageService.getDirectMessageById(id);
        logger.info(directMessage.toString());
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
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
        directMessageDTO.setDateCreate(LocalDateTime.now());
        DirectMessage directMessage = directMessageDtoService.toEntity(directMessageDTO);
        System.out.println(directMessage);
        directMessageService.saveDirectMessage(directMessage);
        logger.info("Созданное сообщение : {}", directMessage);

        List<User> users = new ArrayList<>();
        users.add(directMessage.getConversation().getAssociatedUser());
        users.add(directMessage.getConversation().getOpeningUser());
        users.forEach(user -> {
            if (user.getOnline().equals(0)) {
                user.getUnreadDirectMessages().add(directMessage);
                userService.updateUser(user);
            }
        });

        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.CREATED);
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
        DirectMessage message = directMessageDtoService.toEntity(messageDTO);
        DirectMessage isCreated = directMessageService.getDirectMessageById(messageDTO.getId());
        if (isCreated == null) {
            logger.warn("Сообщение не найдено");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        message.setDateCreate(isCreated.getDateCreate());
        DirectMessage directMessage = directMessageService.updateDirectMessage(message);
        return new ResponseEntity<>(directMessageDtoService.toDto(directMessage), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: direct message deleted")
            })
    public ResponseEntity<DirectMessageDTO> deleteMessage(@PathVariable Long id) {
        directMessageService.deleteDirectMessage(id);
        logger.info("Удалено сообщение с id = {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<List<DirectMessageDTO>> getMessagesByConversationId(@PathVariable Long id) {
        List<DirectMessage> messages = directMessageService.getMessagesByConversationId(id, false);
        messages.sort(Comparator.comparing(DirectMessage::getDateCreate));
        return new ResponseEntity<>(directMessageDtoService.toDto(messages), HttpStatus.OK);
    }

    @GetMapping(value = "/unread/delete/conversation/{convId}/user/{usrId}")
    public ResponseEntity<?> removeChannelMessageFromUnreadForUser (@PathVariable Long convId, @PathVariable Long usrId) {
        userService.removeDirectMessagesForConversationFromUnreadForUser(convId, usrId);
        return new ResponseEntity<>(userDtoService.toDto(userService.getUserById(usrId)), HttpStatus.OK);
    }
}
