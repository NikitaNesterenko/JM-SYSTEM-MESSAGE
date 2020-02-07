package jm.controller;

import jm.*;
import jm.dto.MessageDTO;
import jm.dto.MessageDtoService;
import jm.dto.SlashCommandDto;
import jm.model.*;
import jm.model.message.DirectMessage;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    @Autowired
    MessageDtoService messageDtoService;

    private Bot bot;



    @PostMapping()
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command){
        String currentCommand = command.getCommand();
        ResponseEntity<?> resp = null;
        if (currentCommand.startsWith("/topic")) {
            resp = setTopic(command.getChannelId(), currentCommand.substring(7));
        } else if (currentCommand.startsWith("/dm ")) {
            String[] words = currentCommand.replaceAll("\\s+"," ").trim().split("\\s");
            String toUserName = words[1].startsWith("@") ? words[1] : null;
            if (toUserName != null) {
                resp = sendDirectMessage(command.getUserId(), toUserName.substring(1),
                        currentCommand.substring(currentCommand.indexOf(toUserName) + toUserName.length() + 1),
                        command.getChannelId());
                resp = null;
            }
        } else if (currentCommand.startsWith("/leave")) {

        }
        System.out.println("asd");
        return resp == null? new ResponseEntity<>(HttpStatus.OK) : resp;
    }


    private ResponseEntity<?> setTopic(Long id, String topic) {
        //Изменение топика канала
        Channel channel = channelService.getChannelById(id);
        channel.setTopic("\"" + topic + "\"");
        channelService.updateChannel(channel);

        //отправка сообщения ботом
        return sendRequestMessage(id, "Topic was changed");
    }

    private ResponseEntity<?> sendDirectMessage(Long fromId, String toUsername, String message, Long channelId){
        User toUser = userService.getUserByName(toUsername);

        if (toUser == null) {
            return sendRequestMessage(channelId, "User @" + toUsername + " not found");
        }

        Channel channel = channelService.getChannelById(channelId);
        Workspace workspace = channel.getWorkspace();

        //Получаем conversation двух пользователей, если существовала
        Conversation conv = conversationService.getConversationByUsers(fromId, toUser.getId());
        //создаем новый conversation для пользователей
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

        //создаем DirectMessage
        DirectMessage dm = new DirectMessage();
        dm.setConversation(conv);
        dm.setDateCreate(LocalDateTime.now());
        dm.setUser(userService.getUserById(fromId));
        dm.setContent(message);
        dm.setIsDeleted(false);
        directMessageService.saveDirectMessage(dm);

        return sendRequestMessage(channelId, "Message for @" + toUsername + " was sent");
    }

    //Метод создания сообщения от бота в канале channelId
    private ResponseEntity<?> sendRequestMessage(Long channelId, String message){
        Message newMessage = new Message();
        newMessage.setBot(getBot());
        newMessage.setDateCreate(LocalDateTime.now());
        newMessage.setIsDeleted(false);
        newMessage.setContent(message);
        newMessage.setChannelId(channelId);
        newMessage.setRecipientUsers(new HashSet<>());
        messageService.createMessage(newMessage);
        return new ResponseEntity<>(messageDtoService.toDto(newMessage), HttpStatus.CREATED);
    }

    private Bot getBot() {
        if (bot == null) {
            bot = botService.getBotById(2L);
        }
        return bot;
    }

/*    @MessageMapping("/slackbot")
    @SendTo("/topic/slackbot")
    public ResponseEntity<?> sendMessage(@RequestBody SlashCommandDto command) {
        String currentCommand = command.getCommand();
        String newTopic =  currentCommand.substring(7);
        setTopic(command.getChannelId(), newTopic);
        Map<String, String> response = new HashMap<>();
        response.put("command", "topic");
        response.put("topic", newTopic);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/


}
