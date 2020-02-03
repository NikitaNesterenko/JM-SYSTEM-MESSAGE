package jm.controller.rest;

import jm.BotService;
import jm.ChannelService;
import jm.MessageService;
import jm.WorkspaceService;
import jm.dto.BotDTO;
import jm.dto.BotDtoService;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Workspace;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest/api/bot")
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

    // DTO compliant
    @GetMapping("/workspace/{id}")
    public ResponseEntity<List<BotDTO>> getBotByWorkspace(@PathVariable("id") Long id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        //Bot bot = botService.GetBotByWorkspaceId(workspace);
        List<Bot> bots = botService.GetBotsByWorkspaceId(workspace);
        if (bots.isEmpty()) {
            logger.warn("Не удалось найти бота для workspace с id = {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Бот для workspace c id = {}", id);
        //logger.info(bot.toString());
        return new ResponseEntity<>(botDtoService.toDto(bots), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{id}")
    public ResponseEntity<BotDTO> getBotById(@PathVariable("id") Long id) {
        logger.info("Бот с id = {}", id);
        //logger.info(botService.getBotById(id).toString());
        Bot bot = botService.getBotById(id);
        return new ResponseEntity<>(botDtoService.toDto(bot), HttpStatus.OK);
    }

    // DTO compliant
    @PostMapping(value = "/create")
    public ResponseEntity createBot(@RequestBody BotDTO botDto) {
        Bot bot = botDtoService.toEntity(botDto);
        try {
            botService.createBot(bot);
            logger.info("Cозданный bot: {}", bot);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать бота");
            ResponseEntity.badRequest().build();
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    // DTO compliant
    @PutMapping(value = "/update")
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
    public ResponseEntity deleteBot(@PathVariable("id") Long id) {
        botService.deleteBot(id);
        logger.info("Удален бот с id = {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{id}/channels/{name}/messages")
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
    public ResponseEntity<Set<Channel>> getChannels(@PathVariable("id") Long id) {
        Bot bot = botService.getBotById(id);
        return new ResponseEntity<>(botService.getChannels(bot), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/hour")
    public ResponseEntity<List<Message>> getMessagesPerHour(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/day")
    public ResponseEntity<List<Message>> getMessagesPerDay(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/week")
    public ResponseEntity<List<Message>> getMessagesPerWeek(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/{id}/channels/{name}/messages/month")
    public ResponseEntity<List<Message>> getMessagesPerMonth(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        Channel channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.getId(), startDate, endDate), HttpStatus.OK);
    }
}
