package jm.controller.rest;

import jm.BotService;
import jm.WorkspaceService;
import jm.model.Bot;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/bot")
public class BotRestController {

    private BotService botService;
    private WorkspaceService workspaceService;

    @Autowired
    public void setBotService(BotService botService) { this.botService = botService; }

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) { this.workspaceService = workspaceService; }

    @GetMapping("/workspace/{id}")
    public ResponseEntity<Bot> getBotByWorksapce(@PathVariable("id") Long id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        Bot bot = botService.GetBotByWorkspaceId(workspace);
        if(bot == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Bot>(bot, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bot> getBotById(@PathVariable("id") Long id) {
        return new ResponseEntity<Bot>(botService.getBotById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity createBot(@RequestBody Bot bot) {
        botService.createBot(bot);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateBot(@RequestBody Bot bot) {
        Bot existingBot = botService.getBotById(bot.getId());
        if (existingBot == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
           botService.updateBot(bot);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteBot(@PathVariable("id") Long id) {
       botService.deleteBot(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
