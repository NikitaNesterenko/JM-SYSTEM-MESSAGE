package jm.controller;

import jm.*;
import jm.dto.SlashCommandDto;
import jm.model.*;
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
    private ConversationService conversationService;
    @Autowired
    private DirectMessageService directMessageService;
    @Autowired
    private UserService userService;

    private Bot bot;



    @PostMapping
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command) throws URISyntaxException {
        String currentCommand = command.getCommand();
        if (currentCommand.startsWith("/topic")) {
            setTopic(command.getChannel_id(), currentCommand.substring(7));
        } else if (currentCommand.startsWith("/dm ")) {
            String[] words = currentCommand.replaceAll("\\s+"," ").trim().split("\\s");
            String toUserName = words[1].startsWith("@") ? words[1] : null;
            if (toUserName != null) {
                sendDirectMessage(command.getUser_id(), toUserName.substring(1),
                        currentCommand.substring(currentCommand.indexOf(toUserName) + toUserName.length() + 1),
                        command.getChannel_id());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private void setTopic(Long id, String topic) {
        //Изменение топика канала
        Channel channel = channelService.getChannelById(id);
        channel.setTopic("\"" + topic + "\"");
        channelService.updateChannel(channel);

        //отправка сообщения ботом
        sendRequestMessage(id, "Topic was changed");
    }

    private void sendDirectMessage(Long fromId, String toUsername, String message, Long channelId){
        User toUser = userService.getUserByName(toUsername);

        if (toUser == null) {
            return;
        }
        Channel channel = channelService.getChannelById(channelId);
        Workspace workspace = channel.getWorkspace();
        DirectMessage dm = new DirectMessage();
        Conversation conv = conversationService.getConversationByUsers(fromId, toUser.getId());

        if (conv == null) {
            conv = new Conversation();
            conv.setOpeningUser(userService.getUserById(fromId));
            conv.setWorkspace(workspace);
            conv.setAssociatedUser(toUser);
            conv.setShowForAssociated(true);
            conv.setShowForOpener(true);
            conversationService.createConversation(conv);
            conv = conversationService.getConversationByUsers(fromId, toUser.getId());
        }

        dm.setConversation(conv);
        dm.setDateCreate(LocalDateTime.now());
        dm.setUser(userService.getUserById(fromId));
        dm.setContent(message);
        dm.setIsDeleted(false);
        directMessageService.saveDirectMessage(dm);
    }

    private void sendRequestMessage(Long channelId, String message){
        Message newMessage = new Message();
        newMessage.setBot(getBot());
        newMessage.setDateCreate(LocalDateTime.now());
        newMessage.setIsDeleted(false);
        newMessage.setContent(message);
        newMessage.setChannelId(channelId);
        messageService.createMessage(newMessage);
    }

    private Bot getBot() {
        if (bot == null) {
            bot = botService.getBotById(2L);
        }
        return bot;
    }


}
