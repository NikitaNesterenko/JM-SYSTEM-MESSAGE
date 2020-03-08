package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.CommandsBotService;
import jm.dto.SlashCommandDto;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SlackBotRestController {
    private Logger logger = LoggerFactory.getLogger(SlackBotRestController.class);
    private CommandsBotService commandsBotService;

    @Autowired
    public SlackBotRestController(CommandsBotService commandsBotService) {
        this.commandsBotService = commandsBotService;
    }

    @PostMapping("/app/bot/slackbot")
    // обработка команд для бота, которые реализованы не через вебсокет (их на данный момент нет).
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command) {
        String currentCommand = command.getCommand();
        ResponseEntity<?> resp = null;

        return resp == null ? new ResponseEntity<>(HttpStatus.OK) : resp;
    }

    @MessageMapping("/bot/*") //обработка команд, реализованных на вебсокете
    @SendTo("/topic/slackbot")
    public String getWsCommand(@RequestBody SlashCommandDto command, HttpServletRequest request) throws JsonProcessingException {
        Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");
        return commandsBotService.getWsCommand(command, workspace);
    }

}
