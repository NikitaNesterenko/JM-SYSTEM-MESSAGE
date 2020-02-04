package jm.controller.rest;

import jm.*;
import jm.dto.BotDtoService;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/rest/api/slashcommand")
public class SlashCommandRestController {

    private final SlashCommandService slashCommandService;
    private final BotService botService;
    private final WorkspaceService workspaceService;
    private final MessageService messageService;
    private final ChannelService channelService;
    private final BotDtoService botDtoService;

    public static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    @Autowired
    public SlashCommandRestController(SlashCommandService slashCommandService, BotService botService, WorkspaceService workspaceService, MessageService messageService, ChannelService channelService, BotDtoService botDtoService) {
        this.slashCommandService = slashCommandService;
        this.botService = botService;
        this.workspaceService = workspaceService;
        this.messageService = messageService;
        this.channelService = channelService;
        this.botDtoService = botDtoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlashCommand> getSlashCommandById(@PathVariable Long id){
        logger.info("Slash command with id = {}", id);
        return ResponseEntity.ok(slashCommandService.getSlashCommandById(id));
    }

    @GetMapping("/bot/{id}")
    public ResponseEntity<?> getSlashCommandByBotId(@PathVariable Long id){
        logger.info("Slash command for Bot with id = {}", id);
        return ResponseEntity.ok(slashCommandService.getSlashCommandsByBotId(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SlashCommand> getSlashCommandByName(@PathVariable String name){
        logger.info("Slash command with name = {}", name);
        SlashCommand command = slashCommandService.getSlashCommandByName(name);
        return ResponseEntity.ok(command);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SlashCommand>> getAllSlashCommand(){
        logger.info("Getting all SlashCommands");
        return ResponseEntity.ok(slashCommandService.getAllSlashCommands());
    }

    @GetMapping("/workspace/id/{id}")
    public ResponseEntity<?> getSlashCommandsByWorkspace(@PathVariable Long id) {
        logger.info("Getting all SlashCommands for workspace with id = {}", id);
        return ResponseEntity.ok(slashCommandService.getSlashCommandsByWorkspace(workspaceService.getWorkspaceById(id)));
    }


}
