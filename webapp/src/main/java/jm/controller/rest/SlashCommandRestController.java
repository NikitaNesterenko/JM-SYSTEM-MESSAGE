package jm.controller.rest;

import jm.BotService;
import jm.SlashCommandService;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/rest/api/slashcommand")
public class SlashCommandRestController {

    private final SlashCommandService slashCommandService;
    private final BotService botService;

    public static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    public SlashCommandRestController(SlashCommandService slashCommandService, BotService botService) {
        this.slashCommandService = slashCommandService;
        this.botService = botService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getSlashCommandById(@PathVariable Long id) {
        logger.info("Slash command with id = {}", id);
        return ResponseEntity.ok(slashCommandService.getSlashCommandDTOById(id).map(ResponseEntity::ok)
                                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/create")
    public ResponseEntity createSlashCommand(@RequestBody SlashCommandDto slashCommandDto) {
        SlashCommand sc = slashCommandService.getEntityFromDTO(slashCommandDto);
        slashCommandService.createSlashCommand(sc);
        logger.info("Created SlashCommand: {}", sc);
        return new ResponseEntity(new SlashCommandDto(sc), HttpStatus.CREATED);
    }


    @PutMapping("/update")
    public ResponseEntity updateSlashCommand(@RequestBody SlashCommandDto slashCommandDto) {
        SlashCommand slashCommand = slashCommandService.getEntityFromDTO(slashCommandDto);
        Optional<String> slashCommandName = slashCommandService.getSlashCommandNameById(slashCommand.getId());
        return slashCommandName.isPresent() && slashCommandService.updateSlashCommand(slashCommand) ?
                ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSlashCommand(@PathVariable Long id) {
        slashCommandService.deleteSlashCommand(id);
        logger.info("SlashCommand with id: {} was deleted", id);
        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/bot/{id}")
    public ResponseEntity<?> getSlashCommandByBotId(@PathVariable Long id) {
        logger.info("Slash command for Bot with id = {}", id);
        return new ResponseEntity<>(slashCommandService.getSlashCommandDTOByBotId(id).get(), HttpStatus.OK);
    }

    @PostMapping("/bot/{id}")
    public ResponseEntity<?> addSlashCommandToBot(@PathVariable Long id, SlashCommandDto slashCommandDto) {
        Bot bot = botService.getBotById(id);
        if (bot == null) {
            logger.warn("Bot with id = {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        SlashCommand sc = slashCommandService.getEntityFromDTO(slashCommandDto);
        sc.setBot(bot);
        List<SlashCommand> slashCommands = slashCommandService.getSlashCommandsByBotId(id);

        if (slashCommands.stream().anyMatch(command -> command.getName().equals(sc.getName()))) {
            logger.warn("Slash command with name = {} already exist", sc.getName());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            slashCommandService.createSlashCommand(sc);
            bot.getCommands().add(sc);
            botService.updateBot(bot);
            logger.info("Slash command created");
            return new ResponseEntity<>(new SlashCommandDto(sc), HttpStatus.CREATED);
        }
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<?> getSlashCommandByName(@PathVariable String name) {
        logger.info("Slash command with name = {}", name);
        return slashCommandService.getSlashCommandDTOByName(name).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSlashCommand() {
        logger.info("Getting all SlashCommands");
        return new ResponseEntity<>(slashCommandService.getAllSlashCommandDTO(), HttpStatus.OK);
    }

    @GetMapping("/workspace/id/{id}")
    public ResponseEntity<?> getSlashCommandsByWorkspace(@PathVariable Long id) {
        logger.info("Getting all SlashCommands for workspace with id = {}", id);
        return new ResponseEntity<>(slashCommandService.getSlashCommandDTOByWorkspaceId(id).get(), HttpStatus.OK);
    }

    @GetMapping("/bot/id/{id}/")
    public ResponseEntity<?> getSlashCommandsByBot(@PathVariable Long id) {
        logger.info("Getting all SlashCommands for bot with id = {}", id);
        return new ResponseEntity<>(slashCommandService.getSlashCommandDTOByBotId(id).get(), HttpStatus.OK);
    }
}
