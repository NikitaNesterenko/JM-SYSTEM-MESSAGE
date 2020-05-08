package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.BotService;
import jm.SlashCommandService;
import jm.WorkspaceService;
import jm.dto.BotDTO;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/slashcommand")
@Tag(name = "SlashCommands", description = "SlashCommands API")
public class SlashCommandRestController {

    private final SlashCommandService slashCommandService;
    private final WorkspaceService workspaceService;
    private BotService botService;


    public static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    @Autowired
    public SlashCommandRestController(SlashCommandService slashCommandService, WorkspaceService workspaceService, BotService botService) {
        this.slashCommandService = slashCommandService;
        this.workspaceService = workspaceService;
        this.botService = botService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSlashCommandById(@PathVariable Long id) {
        logger.info("Slash command with id = {}", id);
        return ResponseEntity.ok(slashCommandService.getSlashCommandDTOById(id).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/create")
    @Operation(
            operationId = "createSlashCommand",
            summary = "create SlashCommand",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "createSlashCommand created"),
                    @ApiResponse(responseCode = "404", description = "createSlashCommand no created")
            })
    public ResponseEntity<?> createSlashCommand(@RequestBody SlashCommandDto slashCommandDto) {
        SlashCommand sc = slashCommandService.getEntityFromDTO(slashCommandDto);
        if (sc != null) {
            slashCommandService.createSlashCommand(sc);
            logger.info("Created SlashCommand: {}", sc);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/update")
    @Operation(
            operationId = "updateSlashCommand",
            summary = "update SlashCommand",
            responses = {
                    @ApiResponse(responseCode = "200", description = "UPDATE: SlashCommand update"),
                    @ApiResponse(responseCode = "404", description = "UPDATE: SlashCommand not found"),
            })
    public ResponseEntity updateSlashCommand(@RequestBody SlashCommandDto slashCommandDto) {
        // TODO: ПЕРЕДЕЛАТЬ SlashCommand existCommand из базы плучает всю информацию о сущности, а используется только для проверки на существование
        SlashCommand sc = slashCommandService.getEntityFromDTO(slashCommandDto);
        if (!slashCommandService.updateSlashCommand(sc)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.info("Updated command: {}", sc);
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    @Operation(
            operationId = "deleteSlashCommand",
            summary = "delete SlashCommand",
            responses = {
                    @ApiResponse(responseCode = "200", description = "DELETE: SlashCommand delete"),
                    @ApiResponse(responseCode = "404", description = "not delete SlashCommand with such id")
            })
    public ResponseEntity<?> deleteSlashCommand(@PathVariable Long id) {
        if (getSlashCommandById(id).getStatusCode().is2xxSuccessful()) {
            logger.info("SlashCommand with id: {} was deleted", id);
            slashCommandService.deleteSlashCommand(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/bot/{id}")
    @Operation(
            operationId = "getSlashCommandByBotId",
            summary = "get SlashCommand bot by id",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "Returns a list of bot commands by id "),
                    @ApiResponse(responseCode = "404", description = "not found list of bot commands by id ")
            })
    public ResponseEntity<List<SlashCommandDto>> getSlashCommandByBotId(@PathVariable Long id) {
        final List<SlashCommandDto> slashCommandDtoList = slashCommandService.getSlashCommandDTOByBotId(id).get();
        logger.info("Slash command for Bot with id = {}", id);
        return !slashCommandDtoList.isEmpty()
                ? new ResponseEntity<>(slashCommandDtoList, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/bot/{id}")
    @Operation(
            operationId = "addSlashCommandToBot",
            summary = "SlashCommand the bot by id",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "command successfully installed"),
                    @ApiResponse(responseCode = "404", description = "bot by specified id not found"),
                    @ApiResponse(responseCode = "409", description = "team already exists"),
            })
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
    @Operation(
            operationId = "getSlashCommandByName",
            summary = "getSlashCommandByName",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "will return a command by name"),
                    @ApiResponse(responseCode = "404", description = "command with the specified name not found"),
            })
    public ResponseEntity<?> getSlashCommandByName(@PathVariable String name) {
        logger.info("Slash command with name = {}", name);
        return slashCommandService.getSlashCommandDTOByName(name).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    @Operation(
            operationId = "getAllSlashCommand",
            summary = "getAllSlashCommand",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "will return a all commands"),
                    @ApiResponse(responseCode = "404", description = "all commands not found"),
            })
    public ResponseEntity<List<SlashCommandDto>> getAllSlashCommand() {
        final List<SlashCommandDto> slashCommandDtoList = slashCommandService.getAllSlashCommandDTO().get();
        if (!slashCommandDtoList.isEmpty()) {
            logger.info("Getting all SlashCommands");
            return new ResponseEntity<>(slashCommandDtoList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/workspace/id/{id}")
    @Operation(
            operationId = "getSlashCommandsByWorkspace",
            summary = "getSlashCommandsByWorkspace",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "will return all commands to workspace"),
                    @ApiResponse(responseCode = "200", description = "not found all commands to workspace")
            })
    public ResponseEntity<List<SlashCommandDto>> getSlashCommandsByWorkspace(@PathVariable Long id) {
        final List<SlashCommandDto> slashCommandDtoList = slashCommandService.getSlashCommandDTOByWorkspaceId(id).get();
        if (!slashCommandDtoList.isEmpty()) {
            logger.info("Getting all SlashCommands for workspace with id = {}", id);
            return new ResponseEntity<>(slashCommandDtoList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/bot/id/{id}/")
    @Operation(
            operationId = "getSlashCommandsByBot",
            summary = "getSlashCommandsByBot",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SlashCommandDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "will return all bot commands with such id"),
                    @ApiResponse(responseCode = "404", description = "not found all bot commands with such id")
            })
    public ResponseEntity<List<SlashCommandDto>> getSlashCommandsByBot(@PathVariable Long id) {
        final List<SlashCommandDto> slashCommandDtoList = slashCommandService.getSlashCommandDTOByBotId(id).get();
        if (!slashCommandDtoList.isEmpty()) {
            logger.info("Getting all SlashCommands for bot with id = {}", id);
            return new ResponseEntity<>(slashCommandDtoList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
