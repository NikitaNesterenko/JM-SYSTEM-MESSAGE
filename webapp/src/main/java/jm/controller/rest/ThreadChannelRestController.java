package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MessageService;
import jm.ThreadChannelMessageService;
import jm.ThreadChannelService;
import jm.dto.MessageDTO;
import jm.dto.ThreadDTO;
import jm.dto.ThreadMessageDTO;
import jm.model.Message;
import jm.model.ThreadChannel;
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
    private final MessageService messageService;

    public ThreadChannelRestController(ThreadChannelService threadChannelService, ThreadChannelMessageService threadChannelMessageService, MessageService messageService) {
        this.threadChannelService = threadChannelService;
        this.threadChannelMessageService = threadChannelMessageService;
        this.messageService = messageService;
    }

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
    public ResponseEntity<ThreadChannel> createThreadChannel(@RequestBody MessageDTO messageDTO) {
        System.out.println("ТРЕД!");
        // TODO: исправить
        messageDTO.setDateCreateLocalDateTime(LocalDateTime.now());
//        Message message = messageDtoService.toEntity(messageDTO);
        Message message = messageService.getMessageByMessageDTO(messageDTO);
//        Message message = new Message(messageDTO);
        ThreadChannel threadChannel = new ThreadChannel(message);
        System.out.println(threadChannel);
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
    public ResponseEntity<ThreadMessageDTO> createThreadChannelMessage(@RequestBody ThreadMessageDTO threadMessageDTO) {
//        Сохранение сообщения выполняется в MessagesController сразу из websocket
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//    @GetMapping("/messages/{id}")
//    public ResponseEntity<List<ThreadChannelMessage>> findAllThreadChannelMessagesByThreadChannel(@PathVariable Long id) {
//        System.out.println("ID = " + id);
//        ThreadChannel threadChannel = threadChannelService.getThreadChennelById(id);
//        System.out.println("threadChannel = " + threadChannel);
//        return new ResponseEntity<>(threadChannelMessageService.findAllThreadChannelMessagesByThreadChannel(threadChannel)
//                , HttpStatus.OK);
//    }

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
    public ResponseEntity<ThreadDTO> findThreadChannelByChannelMessageId(@PathVariable("message_id") Long id) {
        ThreadChannel temp = threadChannelService.findByChannelMessageId(id);
        System.out.println("GET-THREADCHANNEL - " + temp);
        ThreadDTO threadDTO = new ThreadDTO(temp);
        return new ResponseEntity<>(threadDTO.getId() == null ? null : threadDTO, HttpStatus.OK);
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
    public ResponseEntity<List<ThreadMessageDTO>> findAllThreadChannelMessagesByThreadChannelId(@PathVariable Long id) {
        /*List<ThreadChannelMessage> list = threadChannelMessageService.findAllThreadChannelMessagesByThreadChannelId(id);
        System.out.println("LIST - " + list.toString());*/
        return new ResponseEntity<>(threadChannelMessageService.getAllThreadMessageDTOByThreadChannelId(id), HttpStatus.OK);
    }
}