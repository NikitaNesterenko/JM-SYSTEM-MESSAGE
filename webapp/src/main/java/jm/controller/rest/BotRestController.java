package jm.controller.rest;

import jm.BotService;
import jm.ChannelService;
import jm.MessageService;
import jm.WorkspaceService;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.message.ChannelMessage;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/rest/api/bot")
public class BotRestController {

    private BotService botService;
    private WorkspaceService workspaceService;
    private MessageService messageService;
    private ChannelService channelService;

    private static final Logger logger = LoggerFactory.getLogger(
            BotRestController.class);

    @Autowired
    public void setBotService(BotService botService) { this.botService = botService; }

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) { this.workspaceService = workspaceService; }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/workspace/{id}")
    public ResponseEntity<?> getBotByWorksapce(@PathVariable("id") Long id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        Optional<Bot> bot = botService.GetBotByWorkspaceId(workspace);
        if(bot == null) {
            logger.warn("Не удалось найти бота для workspace с id = {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Бот для workspace c id = {}", id);
        //logger.info(bot.toString());
        return new ResponseEntity<Optional<?>>(bot, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bot> getBotById(@PathVariable("id") Long id) {
        logger.info("Бот с id = {}", id);
        //logger.info(botService.getBotById(id).toString());
        return new ResponseEntity<Bot>(botService.getBotById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity createBot(@RequestBody Bot bot) {
        try {
           botService.createBot(bot);
            logger.info("Cозданный bot: {}", bot);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать бота");
            ResponseEntity.badRequest().build();
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateBot(@RequestBody Bot bot) {
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
    public ResponseEntity<?> createMessage(@PathVariable("id") Long id, @PathVariable("name") String name, @RequestBody ChannelMessage message) {
        Optional<Channel> channel = channelService.getChannelByName(name);
        Bot bot = botService.getBotById(id);
        message.setChannel(channel.get());
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
    public ResponseEntity<List<ChannelMessage>> getMessagesPerHour(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        return getListResponseEntity(botId, channelName, LocalDateTime.now().minusHours(1));
    }

    @GetMapping("/{id}/channels/{name}/messages/day")
    public ResponseEntity<List<ChannelMessage>> getMessagesPerDay(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        return getListResponseEntity(botId, channelName, LocalDateTime.now().minusDays(1));
    }

    @GetMapping("/{id}/channels/{name}/messages/week")
    public ResponseEntity<List<ChannelMessage>> getMessagesPerWeek(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        return getListResponseEntity(botId, channelName, LocalDateTime.now().minusWeeks(1));
    }

    @GetMapping("/{id}/channels/{name}/messages/month")
    public ResponseEntity<List<ChannelMessage>> getMessagesPerMonth(@PathVariable("id") Long botId, @PathVariable("name") String channelName) {
        return getListResponseEntity(botId, channelName, LocalDateTime.now().minusMonths(1));
    }

    // Вынесли повторяющийся код!
    private ResponseEntity<List<ChannelMessage>> getListResponseEntity(@PathVariable("id") Long botId, @PathVariable("name") String channelName, LocalDateTime localDateTime) {
        Optional<Channel> channel = channelService.getChannelByName(channelName);
        Bot bot = botService.getBotById(botId);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = localDateTime;
        return new ResponseEntity<>(messageService.getMessagesByBotIdByChannelIdForPeriod(bot.getId(), channel.get().getId(),startDate, endDate), HttpStatus.OK);
    }
}
