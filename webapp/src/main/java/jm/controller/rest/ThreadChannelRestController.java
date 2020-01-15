package jm.controller.rest;

import jm.ThreadChannelMessageService;
import jm.ThreadChannelService;
import jm.dto.MessageDTO;
import jm.dto.MessageDtoService;
import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/threads")
public class ThreadChannelRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            ThreadChannelRestController.class);

    private ThreadChannelService threadChannelService;
    private ThreadChannelMessageService threadChannelMessageService;
    private MessageDtoService messageDtoService;

    @Autowired
    public void setThreadService(ThreadChannelService threadChannelService) {
        this.threadChannelService = threadChannelService;
    }

    @Autowired
    public void setThreadChannelMessageService(ThreadChannelMessageService threadChannelMessageService) {
        this.threadChannelMessageService = threadChannelMessageService;
    }

    @Autowired
    public void setMessageDtoService(MessageDtoService messageDtoService) {
        this.messageDtoService = messageDtoService;
    }

    @PostMapping("/create")
    public ResponseEntity<ThreadChannel> createThreadChannel(@RequestBody MessageDTO messageDTO) {
        System.out.println("ТРЕД!");
        Message message = messageDtoService.toEntity(messageDTO);
//        Message message = new Message(messageDTO);
        ThreadChannel threadChannel  = new ThreadChannel(message);
        System.out.println(threadChannel);
        threadChannelService.createThreadChannel(threadChannel);
        logger.info("Созданный тред : {}", threadChannel);
        return new ResponseEntity<>(threadChannel, HttpStatus.CREATED);
    }

    @PostMapping("/messages/create")
    public ResponseEntity<ThreadChannelMessage> createThreadChannelMessage(@RequestBody ThreadChannelMessage threadChannelMessage) {
        System.out.println("CREATE!!! - " + threadChannelMessage);
        threadChannelMessageService.createThreadChannelMessage(threadChannelMessage);
        return new ResponseEntity<>(threadChannelMessage, HttpStatus.CREATED);
    }

//    @GetMapping("/messages/{id}")
//    public ResponseEntity<List<ThreadChannelMessage>> findAllThreadChannelMessagesByThreadChannel(@PathVariable Long id) {
//        System.out.println("ID = " + id);
//        ThreadChannel threadChannel = threadChannelService.getThreadChennelById(id);
//        System.out.println("threadChannel = " + threadChannel);
//        return new ResponseEntity<>(threadChannelMessageService.findAllThreadChannelMessagesByThreadChannel(threadChannel)
//                , HttpStatus.OK);
//    }

    @GetMapping("/{message_id}")
    public ResponseEntity<ThreadChannel> findThreadChannelByChannelMessageId(@PathVariable("message_id") Long id) {
        ThreadChannel temp = threadChannelService.findByChannelMessageId(id);
        System.out.println("GET-THREADCHANNEL - " + temp);
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<List<ThreadChannelMessage>> findAllThreadChannelMessagesByThreadChannelId(@PathVariable Long id) {
        List<ThreadChannelMessage> list = threadChannelMessageService.findAllThreadChannelMessagesByThreadChannelId(id);
        System.out.println("LIST - " + list.toString());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}