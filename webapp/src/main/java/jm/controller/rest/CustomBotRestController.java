package jm.controller.rest;

import jm.CommandsBotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jmsm/api")
public class CustomBotRestController {


    private final CommandsBotService commandsBotService;

    public CustomBotRestController(CommandsBotService commandsBotService) {
        this.commandsBotService = commandsBotService;
    }

    @PostMapping("/test.api")
    public ResponseEntity<String> testing() {
        String response = "{\"ok\": true}";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
