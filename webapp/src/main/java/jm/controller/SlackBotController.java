package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.*;
import jm.dto.ChannelDtoService;
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
public class SlackBotController {
    private Logger logger = LoggerFactory.getLogger(SlackBotController.class);
    private ChannelService channelService;
    private MessageService messageService;
    private BotService botService;
    private ConversationService conversationService;
    private DirectMessageService directMessageService;
    private UserService userService;
    private MessageDtoService messageDtoService;
    private ChannelDtoService channelDtoService;



    private Bot bot;

    @Autowired
    public SlackBotController(ChannelService channelService, MessageService messageService, BotService botService,
                              ConversationService conversationService, DirectMessageService directMessageService,
                              UserService userService, MessageDtoService messageDtoService, ChannelDtoService channelDtoService) {
        this.channelService = channelService;
        this.messageService = messageService;
        this.botService = botService;
        this.conversationService = conversationService;
        this.directMessageService = directMessageService;
        this.userService = userService;
        this.messageDtoService = messageDtoService;
        this.channelDtoService = channelDtoService;
    }

    @PostMapping("/app/bot/slackbot")
    public ResponseEntity<?> getCommand(@RequestBody SlashCommandDto command){
        String currentCommand = command.getCommand();
        ResponseEntity<?> resp = null;
        if (currentCommand.startsWith("/dm ")) {
            String[] words = currentCommand.replaceAll("\\s+"," ").trim().split("\\s");
            String toUserName = words[1].startsWith("@") ? words[1] : null;
            if (toUserName != null) {
                resp = sendDirectMessage(command.getUserId(), toUserName.substring(1),
                        currentCommand.substring(currentCommand.indexOf(toUserName) + toUserName.length() + 1),
                        command.getChannelId());
            } else {
                resp = sendRequestMessage(command.getChannelId(), "Command is incorrect");
            }
        }
        return resp == null? new ResponseEntity<>(HttpStatus.OK) : resp;
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

    @MessageMapping("/slackbot")
    @SendTo("/topic/slackbot")
    public String getWsCommand(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        String currentCommand = command.getCommand();
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        if (currentCommand.startsWith("/topic")) {
            String newTopic =  currentCommand.substring(7);
            setTopic(command.getChannelId(), newTopic);
            response.put("command", "topic");
            response.put("topic", newTopic);
            response.put("channelId", command.getChannelId().toString());
        } else if (currentCommand.startsWith("/leave")) {
            String commandBody = currentCommand.length() > 6 ? currentCommand.substring(7) : "";
            String channelName = commandBody.replaceAll("\\s+"," ").trim();
            Channel channel = channelService.getChannelByName(channelName);
            if (channel == null) {
                channel = channelService.getChannelById(command.getChannelId());
            }
            response.put("report",leaveChannel(channel, command.getUserId()));
            response.put("command", "leave");
            response.put("userId", command.getUserId().toString());
        } else if (currentCommand.startsWith("/join")) {
            String commandBody = currentCommand.length() > 5 ? currentCommand.substring(6) : "";
            String channelName = commandBody.replaceAll("\\s+"," ").trim();
            Channel channel = channelService.getChannelByName(channelName);
            if (channel != null) {
                response.put("report", joinChannel(channel, command.getUserId()));
                response.put("command", "join");
                response.put("status", "OK");
                response.put("userId", command.getUserId().toString());
                response.put("channelId", channel.getId().toString());
                response.put("channel", mapper.writeValueAsString(channelDtoService.toDto(channel)));
            } else {
                response.put("command", "join");
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), "Command is incorrect"));
                response.put("userId", command.getUserId().toString());
            }

        }
        return mapper.writeValueAsString(response);
    }

    private void setTopic(Long ChannelId, String topic) {
        //Изменение топика канала
        Channel channel = channelService.getChannelById(ChannelId);
        channel.setTopic("\"" + topic + "\"");
        channelService.updateChannel(channel);

    }

    private String leaveChannel(Channel channel, Long userId) throws JsonProcessingException {
        User user = userService.getUserById(userId);
        channel.getUsers().remove(user);
        channelService.updateChannel(channel);
        return sendRequestMessage(channel.getId(), user, "Was left the channel");
    }

    private String sendRequestMessage(Long channelId, Object author, String reportMsg) throws JsonProcessingException {
        Message newMessage = new Message();
        if (author instanceof User) {
            newMessage.setUser((User) author);
        }
        if (author instanceof Bot) {
            newMessage.setBot((Bot) author);
        }
        newMessage.setDateCreate(LocalDateTime.now());
        newMessage.setIsDeleted(false);
        newMessage.setContent(reportMsg);
        newMessage.setChannelId(channelId);
        newMessage.setRecipientUsers(new HashSet<>());
        messageService.createMessage(newMessage);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageDtoService.toDto(newMessage));
    }

    private String joinChannel(Channel channel, Long userId) throws JsonProcessingException {
        User user = userService.getUserById(userId);
        if (!channel.getUsers().contains(user)) {
            channel.getUsers().add(user);
            channelService.updateChannel(channel);
            return sendRequestMessage(channel.getId(), user, "Joined to channel");
        }
        return null;
    }

    private String sendTempRequestMessage(Long channelId, Object author, String reportMsg) throws JsonProcessingException {
        Message newMessage = new Message();
        if (author instanceof User) {
            newMessage.setUser((User) author);
        }
        if (author instanceof Bot) {
            newMessage.setBot((Bot) author);
        }
        newMessage.setDateCreate(LocalDateTime.now());
        newMessage.setIsDeleted(false);
        newMessage.setContent(reportMsg);
        newMessage.setChannelId(channelId);
        newMessage.setRecipientUsers(new HashSet<>());
        //messageService.createMessage(newMessage);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageDtoService.toDto(newMessage));
    }


}
