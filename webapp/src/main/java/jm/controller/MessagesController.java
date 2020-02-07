package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.ChannelService;
import jm.MessageService;
import jm.UserService;
import jm.dto.*;
import jm.model.Channel;
import jm.model.InputMessage;
import jm.model.Message;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Controller
public class MessagesController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageDtoService messageDtoService;



    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String messageCreation(InputMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }

    @MessageMapping("/thread")
    @SendTo("/topic/threads")
    public String threadCreation(ThreadMessageDTO message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }

    @MessageMapping("/direct_message")
    @SendTo("/topic/dm")
    public String directMessageCreation(DirectMessageDTO message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message);
    }

    @MessageMapping("/slackbot")
    @SendTo("/topic/slackbot")
    public String sendMessage(@RequestBody SlashCommandDto command) throws JsonProcessingException {
        String currentCommand = command.getCommand();
        Map<String, String> response = new HashMap<>();
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
        }
        ObjectMapper mapper = new ObjectMapper();
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
        return sendRequestMessage(channel.getId(), user);
    }

    private String sendRequestMessage(Long channelId, User user) throws JsonProcessingException {
        Message newMessage = new Message();
        newMessage.setUser(user);
        newMessage.setDateCreate(LocalDateTime.now());
        newMessage.setIsDeleted(false);
        newMessage.setContent("was left channel");
        newMessage.setChannelId(channelId);
        newMessage.setRecipientUsers(new HashSet<>());
        messageService.createMessage(newMessage);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageDtoService.toDto(newMessage));
    }
}
