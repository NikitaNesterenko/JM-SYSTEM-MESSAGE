package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.*;
import jm.dto.BotDTO;
import jm.dto.BotDtoService;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/bot")
@Tag(name = "bot", description = "Bot API")
public class BotRestController {

    private final BotService botService;
    private final WorkspaceService workspaceService;
    private final MessageService messageService;
    private final ChannelService channelService;
    private final BotDtoService botDtoService;

    private static final Logger logger = LoggerFactory.getLogger(BotRestController.class);

    public BotRestController(BotService botService, WorkspaceService workspaceService, MessageService messageService, ChannelService channelService, BotDtoService botDtoService) {
        this.botService = botService;
        this.workspaceService = workspaceService;
        this.messageService = messageService;
        this.channelService = channelService;
        this.botDtoService = botDtoService;
    }


    @GetMapping("/generate.token")
    @Operation(
            operationId = "generateApiToken",
            summary = "Get token",
            description = "Generate string token:{token} with 128-bit UUID token",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            ),
                            description = "OK: got token"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: token not found")
            })
    public ResponseEntity<String> generateApiToken(){
        String token = "{\"token\":\"" + UUID.randomUUID().toString() + "\"}";
        return new ResponseEntity<>(token, HttpStatus.OK);
    }


    @Autowired
    private UserService userService;

    @PostMapping("/test.send")
    @Operation( // нужно ли на тестовый метод?
            operationId = "testingSend",
            summary = "Testing message from user",
            description = "Generate string message from user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            ),
                            description = "OK: got message"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: testing message not found")
            })
    public ResponseEntity<String> testingSend(){
        User user = userService.getUserById(1L);
        Message message = new Message();
        message.setChannelId(1L);
        message.setUser(user);
        message.setContent("Hello, it's testing message from " + user.getUsername());
        message.setDateCreate(LocalDateTime.now());
        message.setWorkspaceId(1L);

        messageService.createMessage(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/workspace/{id}")
    @Operation(
            operationId = "getBotByWorkspace",
            summary = "Get bot by workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = BotDTO.class)
                            ),
                            description = "OK: get bot"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: bot not found")
            })
    public ResponseEntity<List<BotDTO>> getBotByWorkspace(@PathVariable("id") Long id) {
        List<Bot> bots = botService.getBotsByWorkspaceId(id);
        if (bots.isEmpty()) {
            logger.warn("Не удалось найти ботов для workspace с id = {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Боты для workspace c id = {}", id);
        return new ResponseEntity<>(botDtoService.toDto(bots), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{id}")
    @Operation(
            operationId = "getBotById",
            summary = "Get bot by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BotDTO.class)
                            ),
                            description = "OK: get bot")
            })
    public ResponseEntity<BotDTO> getBotById(@PathVariable("id") Long id) {
        logger.info("Бот с id = {}", id);
        //logger.info(botService.getBotById(id).toString());
        Bot bot = botService.getBotById(id);
        return new ResponseEntity<>(botDtoService.toDto(bot), HttpStatus.OK);
    }

    // DTO compliant
    @PostMapping(value = "/create")
    @Operation(
            operationId = "createBot",
            summary = "Create bot",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BotDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "CREATED: bot created"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: failed to create bot")
            })
    public ResponseEntity<BotDTO> createBot(@RequestBody BotDTO botDto) {
        Bot bot = botDtoService.toEntity(botDto);
        try {
            bot = botService.createBot(bot);
            logger.info("Cозданный bot: {}", bot);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать бота");
            ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(botDtoService.toDto(bot), HttpStatus.CREATED);
    }

    // DTO compliant
    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateBot",
            summary = "Update bot",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BotDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: bot updated"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: bot not found")
            })
    public ResponseEntity updateBot(@RequestBody BotDTO botDto) {
        Bot bot = botDtoService.toEntity(botDto);
        Bot existingBot = botService.getBotById(bot.getId());
        if (existingBot == null) {
            logger.warn("Бот не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            botService.updateBot(bot);
            logger.info("Обновлнный бот: {}", bot);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            operationId = "deleteBot",
            summary = "Delete bot",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: bot deleted")
            })
    public ResponseEntity deleteBot(@PathVariable("id") Long id) {
        botService.deleteBot(id);
        logger.info("Удален бот с id = {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{id}/channels/{name}/messages")
    @Operation(
            operationId = "createMessage",
            summary = "Create message",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Message.class)
                            )
                    ),
                    @ApiResponse(responseCode = "201", description = "CREATED: bot message created")
            })
    public ResponseEntity createMessage(@PathVariable("id") Long id, @PathVariable("name") String name, @RequestBody Message message) {
        Channel channel = channelService.getChannelByName(name);
        Bot bot = botService.getBotById(id);
        message.setChannelId(channel.getId());
        message.setBot(bot);
        message.setDateCreate(LocalDateTime.now());
        messageService.createMessage(message);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/{id}/channels")
    @Operation(
            operationId = "getChannels",
            summary = "Get channels",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Channel.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: get channels by bot")
            })
    public ResponseEntity<Set<Channel>> getChannels(@PathVariable("id") Long id) {
        Bot bot = botService.getBotById(id);
        return new ResponseEntity<>(botService.getChannels(bot), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/hour")
    @Operation(
            operationId = "getMessagesPerHour",
            summary = "Get messages per hour",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Message.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: get bot messages per hour")
            })
    public ResponseEntity<List<Message>> getMessagesPerHour(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate, false), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/day")
    @Operation(
            operationId = "getMessagesPerDay",
            summary = "Get messages per day",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Message.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: get bot messages per day")
            })
    public ResponseEntity<List<Message>> getMessagesPerDay(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate, false), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/week")
    @Operation(
            operationId = "getMessagesPerWeek",
            summary = "Get messages per week",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Message.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: get bot messages per week")
            })
    public ResponseEntity<List<Message>> getMessagesPerWeek(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate, false), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/month")
    @Operation(
            operationId = "getMessagesPerMonth",
            summary = "Get messages per month",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Message.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: get bot messages per month")
            })
    public ResponseEntity<List<Message>> getMessagesPerMonth(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate, false), HttpStatus.OK);
    }
}
