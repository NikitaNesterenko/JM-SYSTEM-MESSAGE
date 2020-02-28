package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.CommandsBotService;
import jm.dto.SlashCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jmsm/api")
public class CustomBotRestController {

    @Autowired
    private CommandsBotService commandsBotService;

    @PostMapping("/test.api")
    public ResponseEntity<String> testing(){
        String response = "{\"ok\": true}";
        System.out.println("response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO если отправлять сообщение из постмана пока что надо SlashCommandDto заменить на String, иначе получится ошибка
    @PostMapping("/send")
    public ResponseEntity sendMsg(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        System.out.println("command: " + command);
        commandsBotService.getWsCommand(command);
        return new ResponseEntity(HttpStatus.OK);
    }
}
