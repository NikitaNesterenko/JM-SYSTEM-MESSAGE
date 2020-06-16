package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.CommandsBotService;
import jm.GoogleDriveSlashCommand;
import jm.TrelloSlashCommand;
import jm.dto.SlashCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackBotRestController {

    private CommandsBotService commandsBotService;
    private TrelloSlashCommand trelloSlashCommand;
    private GoogleDriveSlashCommand googleDriveSlashCommand;

    @Autowired
    public SlackBotRestController(CommandsBotService commandsBotService,
                                  TrelloSlashCommand trelloSlashCommand,
                                  GoogleDriveSlashCommand googleDriveSlashCommand) {
        this.commandsBotService = commandsBotService;
        this.trelloSlashCommand = trelloSlashCommand;
        this.googleDriveSlashCommand = googleDriveSlashCommand;
    }

    @PostMapping("/app/bot/slackbot")
    // обработка команд для бота, которые реализованы не через вебсокет (их на данный момент нет).
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command) {
        String currentCommand = command.getCommand();
        return currentCommand != null && !currentCommand.equals("")
                ? ResponseEntity.ok(command)
                : ResponseEntity.badRequest().build();
    }

    @MessageMapping("/bot/*") //обработка команд, реализованных на вебсокете
    @SendTo("/topic/slackbot")
    public String getWsCommand(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        return commandsBotService.getWsCommand(command);
    }

    @MessageMapping("/bot/trello")
    @SendTo("/topic/trello")
    public String test(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        System.out.println(command);
        return trelloSlashCommand.getCommand(command);
    }

    //соотносит команду передаваемую в форме отправки сообщений с командой в БД в таблице slash_commands
    @MessageMapping("/bot/google_drive")
    @SendTo("/topic/google_drive")
    public String test2(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        System.out.println(command);
        return googleDriveSlashCommand.getCommand(command);
    }

}
