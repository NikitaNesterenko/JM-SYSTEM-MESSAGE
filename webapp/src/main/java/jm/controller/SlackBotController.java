package jm.controller;

import jm.*;
import jm.dto.SlashCommandDto;
import jm.model.Channel;
import jm.model.Message;
import jm.model.SlashCommand;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/app/bot/slackbot")
public class SlackBotController {
    private Logger logger = LoggerFactory.getLogger(SlackBotController.class);
    @Autowired
    private ChannelService channelService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private BotService botService;
    @Autowired
    private SlashCommandService slashCommandService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private DirectMessageService directMessageService;
    @Autowired
    private UserService userService;



    @PostMapping
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command) throws URISyntaxException {

        if (command.getCommand().startsWith("/topic")) {
            setTopic(command.getChannel_id(), command.getCommand().substring(7));
        } else if (command.getCommand().startsWith("/dm")) {
            //sendDirectMessage();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private void setTopic(Long id, String topic) {
        Channel channel = channelService.getChannelById(id);
        channel.setTopic("\"" + topic + "\"");
        channelService.updateChannel(channel);
        Message message = new Message();
        message.setBot(botService.getBotById(2L));
        message.setDateCreate(LocalDateTime.now());
        message.setIsDeleted(false);
        message.setContent("Topic was changed");
        message.setChannelId(id);
        messageService.createMessage(message);
    }

    private void sendDirectMessage(Long fromId, String toUsername, String message, Long channelId){
/*        Channel channel = channelService.getChannelById(channelId);
        User toUser = userService.;
        Long wsId = channel.getWorkspace().getId();
        DirectMessage dm = new DirectMessage();
        dm.setConversation();*/


    }
}
