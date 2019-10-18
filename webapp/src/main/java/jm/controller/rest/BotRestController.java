package jm.controller.rest;

import jm.BotService;
import jm.model.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bot")
public class BotRestController {

    BotService botService;

    @Autowired
    public void setBotService(BotService botService) { this.botService = botService; }

    @GetMapping
    public ResponseEntity<List<Bot>> getBots() {
        return new ResponseEntity<>(botService.gelAllBots(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bot> getBotById(@PathVariable("id") Long id) {
        return new ResponseEntity<Bot>(botService.getBotById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createBot(@RequestBody Bot bot) {
        botService.createBot(bot);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateBot(@RequestBody Bot bot) {
        Bot existingBot = botService.getBotById(bot.getId());
        if (existingBot == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
           botService.updateBot(bot);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBot(@PathVariable("id") Long id) {
       botService.deleteBot(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
