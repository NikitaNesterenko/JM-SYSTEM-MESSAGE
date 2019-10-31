package jm.controller.rest;

import jm.BotService;
import jm.model.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bot")
public class BotRestController {
    private static final Logger logger = LoggerFactory.getLogger(
            BotRestController.class);
    BotService botService;

    @Autowired
    public void setBotService(BotService botService) {
        this.botService = botService;
    }

    @GetMapping
    public ResponseEntity<List<Bot>> getBots() {
        logger.info("Список ботов : ");
        for (Bot bot: botService.gelAllBots()) {
            logger.info(bot.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(botService.gelAllBots(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bot> getBotById(@PathVariable("id") Long id) {
        logger.info("Бот с id = {}",id);
        logger.info(botService.getBotById(id).toString());
        return new ResponseEntity<Bot>(botService.getBotById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createBot(@RequestBody Bot bot) {
        botService.createBot(bot);
        logger.info("Созданный бот: {}",bot);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateBot(@RequestBody Bot bot) {
        Bot existingBot = botService.getBotById(bot.getId());
        logger.info("Существующий бот: {}",existingBot);
        if (existingBot == null) {
            logger.warn("Бот не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            botService.updateBot(bot);
            logger.info("Обновленный бот: {}", bot);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBot(@PathVariable("id") Long id) {
        botService.deleteBot(id);
        logger.info("id удаленного бота: {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
