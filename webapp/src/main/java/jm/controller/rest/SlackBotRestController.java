package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.CommandsBotService;
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

    @Autowired
    public SlackBotRestController(CommandsBotService commandsBotService) {
        this.commandsBotService = commandsBotService;
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

}
