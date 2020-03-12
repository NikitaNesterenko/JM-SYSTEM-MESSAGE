package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MessageService;
import jm.ThreadChannelMessageService;
import jm.ThreadChannelService;
import jm.dto.*;
import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rest/api/threads")
@Tag(name = "thread", description = "Thread Channel API")
public class ThreadChannelRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            ThreadChannelRestController.class);

    private final ThreadChannelService threadChannelService;
    private final ThreadChannelMessageService threadChannelMessageService;
    //    private MessageDtoService messageDtoService;
    private final ThreadMessageDtoService threadMessageDtoService;
    private final ThreadDtoService threadDtoService;
    private final MessageService messageService;

    public ThreadChannelRestController (ThreadChannelService threadChannelService, ThreadChannelMessageService threadChannelMessageService, ThreadMessageDtoService threadMessageDtoService, ThreadDtoService threadDtoService, MessageService messageService) {
        this.threadChannelService = threadChannelService;
        this.threadChannelMessageService = threadChannelMessageService;
        this.threadMessageDtoService = threadMessageDtoService;
        this.threadDtoService = threadDtoService;
        this.messageService = messageService;
    }


//    @Autowired
//    public void setThreadDtoService (ThreadDtoService threadDtoService) {
//        this.threadDtoService = threadDtoService;
//    }
//
//    @Autowired
//    public void setThreadMessageDtoService (ThreadMessageDtoService threadMessageDtoService) {
//        this.threadMessageDtoService = threadMessageDtoService;
//    }
//
//    @Autowired
//    public void setThreadService (ThreadChannelService threadChannelService) {
//        this.threadChannelService = threadChannelService;
//    }
//
//    @Autowired
//    public void setThreadChannelMessageService (ThreadChannelMessageService threadChannelMessageService) {
//        this.threadChannelMessageService = threadChannelMessageService;
//    }
//
//    @Autowired
//    public void setMessageDtoService (MessageDtoService messageDtoService) {
//        this.messageDtoService = messageDtoService;
//    }

    @PostMapping("/create")
    @Operation(summary = "Create thread channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadChannel.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "thread channel created")
            })
    public ResponseEntity<ThreadChannel> createThreadChannel (@RequestBody MessageDTO messageDTO) {

        Message message = messageService.getMessageByMessageDTO(messageDTO);
        message.setDateCreate(LocalDateTime.now());

        ThreadChannel threadChannel = new ThreadChannel(message);

        threadChannelService.createThreadChannel(threadChannel);
        logger.info("Созданный тред : {}", threadChannel);
        return new ResponseEntity<>(threadChannel, HttpStatus.CREATED);
    }

    @PostMapping("/messages/create")
    @Operation(summary = "Create thread channel message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadMessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "thread channel message created")
            })
    public ResponseEntity<ThreadMessageDTO> createThreadChannelMessage (@RequestBody ThreadMessageDTO threadMessageDTO) {
        // TODO: проверить
        // Сохранение сообщения выполняется в MessagesController сразу из websocket

        ThreadChannelMessage threadChannelMessage = threadMessageDtoService.toEntity(threadMessageDTO);
        threadChannelMessageService.createThreadChannelMessage(threadChannelMessage);
        return new ResponseEntity<>(threadMessageDtoService.toDto(threadChannelMessage), HttpStatus.CREATED);
    }

    @GetMapping("/{message_id}")
    @Operation(summary = "Get thread channel by message id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadDTO.class)
                            ),
                            description = "OK: get thread channel"
                    )
            })
    public ResponseEntity<ThreadDTO> findThreadChannelByChannelMessageId (@PathVariable("message_id") Long id) {
        ThreadChannel temp = threadChannelService.findByChannelMessageId(id);
        return new ResponseEntity<>(threadDtoService.toDto(temp), HttpStatus.OK);
    }

    @GetMapping("/messages/{id}")
    @Operation(summary = "Get thread channel message by thread channel id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ThreadMessageDTO.class)
                            ),
                            description = "OK: find thread channel messages"
                    )
            })
    public ResponseEntity<List<ThreadMessageDTO>> findAllThreadChannelMessagesByThreadChannelId (@PathVariable Long id) {
        List<ThreadChannelMessage> list = threadChannelMessageService.findAllThreadChannelMessagesByThreadChannelId(id);
        return new ResponseEntity<>(threadMessageDtoService.toDto(list), HttpStatus.OK);
    }
}