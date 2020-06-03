package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MessageService;
import jm.ThreadChannelMessageService;
import jm.ThreadChannelService;
import jm.component.Response;
import jm.dto.MessageDTO;
import jm.dto.ThreadDTO;
import jm.dto.ThreadMessageDTO;
import jm.model.ThreadChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    @Operation(
            operationId = "createThreadChannel",
            summary = "Create thread channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadChannel.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "thread channel created")
            })
    public Response<ThreadChannel> createThreadChannel(@RequestBody MessageDTO messageDTO) {
        // TODO: ПЕРЕДЕЛАТЬ сразу получать из базы ThreadChannel threadChannel
        if (messageDTO.getId() != null) {
            messageDTO.setDateCreateLocalDateTime(LocalDateTime.now());
            ThreadChannel threadChannel = new ThreadChannel(messageDTO.getId());
            threadChannelService.createThreadChannel(threadChannel);
            logger.info("Созданный тред : {}", threadChannel);
            return Response.ok(threadChannel);
        }
        return Response.error(HttpStatus.BAD_REQUEST, "Error create thread channel");
    }

    @PostMapping("/messages/create")
    @Operation(
            operationId = "createThreadChannelMessage",
            summary = "Create thread channel message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadMessageDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "thread channel message created")
            })
    public Response<ThreadMessageDTO> createThreadChannelMessage(@RequestBody ThreadMessageDTO threadMessageDTO) {
//        Сохранение сообщения выполняется в MessagesController сразу из websocket
        return Response.ok().build();
    }

    @GetMapping("/{message_id}")
    @Operation(
            operationId = "findThreadChannelByChannelMessageId",
            summary = "Get thread channel by message id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ThreadDTO.class)
                            ),
                            description = "OK: get thread channel"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no thread with this message id")
            })
    public Response<ThreadDTO> findThreadChannelByChannelMessageId(@PathVariable("message_id") Long id) {
        // TODO: ПЕРЕДЕЛАТЬ сразу получать из базы ThreadDTO
        try {
            return Response.ok(new ThreadDTO(threadChannelService.findByChannelMessageId(id)));
        } catch (Exception e) {
            return Response.error(HttpStatus.BAD_REQUEST, "Error find thread channel");
        }
    }

    @GetMapping("/messages/{id}")
    @Operation(
            operationId = "findAllThreadChannelMessagesByThreadChannelId",
            summary = "Get thread channel message by thread channel id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ThreadMessageDTO.class)
                            ),
                            description = "OK: find thread channel messages"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no ThreadMessageDTO")
            })
    public Response<List<ThreadMessageDTO>> findAllThreadChannelMessagesByThreadChannelId(@PathVariable Long id) {
        List<ThreadMessageDTO> threadMessageDTOList = threadChannelMessageService.getAllThreadMessageDTOByThreadChannelId(id);
        return threadMessageDTOList != null && !threadMessageDTOList.isEmpty()
                ? Response.ok(threadMessageDTOList)
                : Response.error(HttpStatus.BAD_REQUEST, "Error find all tread channel message");
    }
}