package jm.controller.rest;

import jm.*;
import jm.dto.BotDtoService;
import jm.dto.SlashCommandDto;
import jm.dto.SlashCommandDtoService;
import jm.model.Bot;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/rest/api/slashcommand")
public class SlashCommandRestController {

    private final SlashCommandService slashCommandService;
    private final WorkspaceService workspaceService;
    private final SlashCommandDtoService slashCommandDtoService;
    private BotService botService;


    public static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    @Autowired
    public SlashCommandRestController(SlashCommandService slashCommandService, WorkspaceService workspaceService, SlashCommandDtoService slashCommandDtoService, BotService botService) {
        this.slashCommandService = slashCommandService;
        this.workspaceService = workspaceService;
        this.slashCommandDtoService = slashCommandDtoService;
        this.botService = botService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSlashCommandById(@PathVariable Long id) {
        logger.info("Slash command with id = {}", id);
        return ResponseEntity.ok(slashCommandDtoService.toDto(slashCommandService.getSlashCommandById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSlashCommand(SlashCommandDto slashCommandDto) {
        SlashCommand sc = slashCommandDtoService.toEntity(slashCommandDto);
        slashCommandService.createSlashCommand(sc);
        logger.info("Created SlashCommand: {}", sc);
        return new ResponseEntity<>(sc, HttpStatus.CREATED);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateSlashCommand(SlashCommandDto slashCommandDto) {
        SlashCommand sc = slashCommandDtoService.toEntity(slashCommandDto);
        SlashCommand existCommand = slashCommandService.getSlashCommandById(sc.getId());
        if (existCommand == null) {
            logger.warn("slashcommand not found");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        logger.info("Existing command: {}", existCommand);
        slashCommandService.updateSlashCommand(sc);
        logger.info("Updated command: {}", sc);
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        slashCommandService.deleteSlashCommand(id);
        logger.info("SlashCommand with id: {} was deleted", id);
        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/bot/{id}")
    public ResponseEntity<?> getSlashCommandByBotId(@PathVariable Long id) {
        logger.info("Slash command for Bot with id = {}", id);
        return ResponseEntity.ok(slashCommandDtoService.toDto(slashCommandService.getSlashCommandsByBotId(id)));
    }

    @PostMapping("/bot/{id}")
    public ResponseEntity<?> addSlashCommandToBot(@PathVariable Long id, SlashCommandDto slashCommandDto) {
        Bot bot = botService.getBotById(id);
        if (bot == null) {
            logger.warn("Bot with id = {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        SlashCommand sc = slashCommandDtoService.toEntity(slashCommandDto);
        sc.setBot(bot);
        List<SlashCommand> slashCommands = slashCommandService.getSlashCommandsByBotId(id);

        if (slashCommands.stream().anyMatch(command -> command.getName().equals(sc.getName()))) {
            logger.warn("Slash command with name = {} already exist", sc.getName());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            slashCommandService.createSlashCommand(sc);
            logger.info("Slash command created");
            return new ResponseEntity<>(sc, HttpStatus.CREATED);
        }
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<?> getSlashCommandByName(@PathVariable String name) {
        logger.info("Slash command with name = {}", name);
        SlashCommand command = slashCommandService.getSlashCommandByName(name);
        return ResponseEntity.ok(slashCommandDtoService.toDto(command));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSlashCommand() {
        logger.info("Getting all SlashCommands");
        return ResponseEntity.ok(slashCommandDtoService.toDto(slashCommandService.getAllSlashCommands()));
    }

    @GetMapping("/workspace/id/{id}")
    public ResponseEntity<?> getSlashCommandsByWorkspace(@PathVariable Long id) {
        logger.info("Getting all SlashCommands for workspace with id = {}", id);
        return ResponseEntity.ok(slashCommandDtoService.toDto(slashCommandService.getSlashCommandsByWorkspace(workspaceService.getWorkspaceById(id))));
    }

    @GetMapping("/bot/id/{id}/")
    public ResponseEntity<?> getSlashCommandsByBot(@PathVariable Long id) {
        logger.info("Getting all SlashCommands for bot with id = {}", id);
        return ResponseEntity.ok(slashCommandDtoService.toDto(slashCommandService.getSlashCommandsByBotId(id)));
    }
}
