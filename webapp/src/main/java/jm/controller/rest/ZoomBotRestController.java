package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.CommandsBotService;
import jm.dto.SlashCommandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZoomBotRestController {

    private CommandsBotService commandsBotService;


    public ZoomBotRestController(CommandsBotService commandsBotService) {
        this.commandsBotService = commandsBotService;
    }

    @PostMapping("/app/bot/zoombot")

    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command) {
        String currentCommand = command.getCommand();
        return currentCommand != null && !currentCommand.equals("")
                ? ResponseEntity.ok(command)
                : ResponseEntity.badRequest().build();
    }

    @MessageMapping("/bot/zoom/*") //обработка команд, реализованных на вебсокете
    @SendTo("/topic/zoombot")
    public String getWsCommand(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        return commandsBotService.getWsCommand(command);
    }


}