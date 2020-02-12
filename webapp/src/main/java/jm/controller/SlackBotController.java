package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jm.*;
import jm.dto.*;
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
import java.util.*;

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
    private DirectMessageDtoService directMessageDtoService;

    private final String INCORRECT_COMMAND = "Command is incorrect";

    private Bot bot;

    @Autowired
    public SlackBotController(ChannelService channelService, MessageService messageService, BotService botService,
                              ConversationService conversationService, DirectMessageService directMessageService,
                              UserService userService, MessageDtoService messageDtoService, ChannelDtoService channelDtoService,
                              DirectMessageDtoService directMessageDtoService) {
        this.channelService = channelService;
        this.messageService = messageService;
        this.botService = botService;
        this.conversationService = conversationService;
        this.directMessageService = directMessageService;
        this.userService = userService;
        this.messageDtoService = messageDtoService;
        this.channelDtoService = channelDtoService;
        this.directMessageDtoService = directMessageDtoService;
    }

/*    @PostMapping("/app/bot/slackbot")
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
    }*/

    private String sendDirectMessage(Long fromId, User toUser, String message, Long channelId) throws JsonProcessingException {

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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(dm);
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
        String currentCommand = command.getCommand().trim();
        Map<String, String> response = new HashMap<>();
        response.put("userId", command.getUserId().toString()); //Id пользователя, отправившего запрос
        ObjectMapper mapper = new ObjectMapper();
        if (currentCommand.startsWith("/topic")) {
            String newTopic =  currentCommand.substring(7);
            setTopic(command.getChannelId(), newTopic);
            response.put("command", "topic");
            response.put("topic", newTopic);
            response.put("channelId", command.getChannelId().toString());
        } else if (currentCommand.startsWith("/leave")) {
            response.put("command", "leave");
            String commandBody = currentCommand.length() > 6 ? currentCommand.substring(7) : "";
            String channelName = commandBody.replaceAll("\\s+"," ").trim();
            Channel channel = channelService.getChannelByName(channelName);
            if (channel == null) {
                if (channelName.equals("")) {
                    channel = channelService.getChannelById(command.getChannelId());
                    response.put("report", leaveChannel(channel, command.getUserId()));
                    response.put("status", "OK");
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), INCORRECT_COMMAND));
                }
            } else {
                response.put("report", leaveChannel(channel, command.getUserId()));
                response.put("status", "OK");
            }
        } else if (currentCommand.startsWith("/join") || currentCommand.startsWith("/open")) {
            response.put("command", "join");
            String commandBody = currentCommand.length() > 5 ? currentCommand.substring(6) : "";
            String channelName = getChannelsNamesFromSmg(currentCommand).size() > 1 ? "" :
                    getChannelsNamesFromSmg(currentCommand).get(0);
            Channel channel = channelService.getChannelByName(channelName);
            if (channel != null) {
                response.put("report", joinChannel(channel, command.getUserId()));
                response.put("status", "OK");
                response.put("channelId", channel.getId().toString());
                response.put("channel", mapper.writeValueAsString(channelDtoService.toDto(channel)));
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), INCORRECT_COMMAND));
            }

        } else if (currentCommand.startsWith("/shrug")) {
            response.put("command", "shrug");
            String messText = currentCommand.length() > 6 ? currentCommand.substring(6).trim() : "";
            response.put("report", sendRequestMessage(command.getChannelId(),
                    userService.getUserById(command.getUserId()), messText + " ¯\\_(ツ)_/¯"));
            response.put("status", "OK");
        } else if (currentCommand.startsWith("/invite")) {
            response.put("command", "invite");
            String commandBody = currentCommand.length() > 9 ? currentCommand.substring(8).replaceAll("\\s+"," ").trim() : "";
            String[] words = commandBody.split("\\s+");
            List<User> invitedUsers = new ArrayList<>(); //список пользователей, которыхприглашаем
            List<String> channelsName = new ArrayList<>(); //список каналов, куда приглашаем, выбираем только первый (пока что?)
            Channel channelToInvite;
            //пробегаемся по сообщению и вытаскиваем ники пользователей (отмечены @) и имена каналов (отмечены №)
            Arrays.asList(words).forEach(word -> {
                if (word.startsWith("@")) {
                    invitedUsers.add(userService.getUserByName(word.substring(1)));
                } else  {
                    channelsName.add(word);
                }
            });
            //если канал не указан, то выбираем канал, в который отправлена команда, иначе выбираем первый упомянутый канал
            if (!channelsName.isEmpty()) {
                channelToInvite = channelService.getChannelByName(channelsName.get(0));
            } else {
                channelToInvite = channelService.getChannelById(command.getChannelId());
            }
            //убираем всех существующих на канале пользователей
            invitedUsers.removeAll(channelToInvite.getUsers());
            //если список пользователей пуст или указанный канал не найден отправляем ошибку
            if (channelToInvite != null && !invitedUsers.isEmpty()) {
                response.put("report", inviteUsersToChannel(invitedUsers, channelToInvite, command.getUserId()));
                response.put("status", "OK");
                List<Long> finalUserIds = new ArrayList<>();
                invitedUsers.forEach(user -> finalUserIds.add(user.getId()));
                response.put("targets", mapper.writeValueAsString(finalUserIds)); //id добавленный пользователей
                response.put("channel", mapper.writeValueAsString(channelToInvite)); //канал, куда добавляли пользователей
                response.put("channelId", command.getChannelId().toString()); //id канала, откуда отправлена команда
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), channelToInvite == null ?
                        "Channel not found" : "Users list is empty or all users are already in channel"));
            }
        } else if (currentCommand.startsWith("/who")) {
            response.put("command", "who");

            if (currentCommand.split("\\s+")[0].equals("/who")) {
                Channel currentChannel = channelService.getChannelById(command.getChannelId());
                response.put("report", whoAreInChannel(currentChannel, command.getUserId()));
                response.put("status", "OK");
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), INCORRECT_COMMAND));
            }
        } else if (currentCommand.startsWith("/remove") || currentCommand.startsWith("/kick")) {
            response.put("command", "kick");
            response.put("channelId", command.getChannelId().toString());
            Channel currentChannel = channelService.getChannelById(command.getChannelId());
            List<User> kickedUser = new ArrayList<>();
            getUserNamesFromMessage(currentCommand).forEach(userName -> {
                User user = userService.getUserByName(userName);
                if (currentChannel.getUsers().contains(user)) {
                    kickedUser.add(user);
                }
            });
            if (kickedUser.size() > 0) {
                response.put("status", "OK");
                response.put("report", kickUsers(kickedUser, currentChannel, command.getUserId()));
                response.put("kickedUsersIds", mapper.writeValueAsString(kickedUser.stream().map(user -> user.getId()).toArray()));
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(currentChannel.getId(), getBot(), "Users not found"));
            }
        } else if (currentCommand.startsWith("/msg ")) {
            response.put("command", "msg");
            String targetChannelName = getChannelsNamesFromSmg(currentCommand).get(0);
            Channel targetChannel = channelService.getChannelByName(targetChannelName);
            if (targetChannel != null) {
                response.put("status", "OK");
                response.put("report", sendRequestMessage(targetChannel.getId(), userService.getUserById(command.getUserId()),
                        currentCommand.substring(currentCommand.indexOf(targetChannelName) + targetChannelName.length())));
                response.put("channelId", targetChannel.getId().toString());
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), INCORRECT_COMMAND));
            }
        } else if (currentCommand.startsWith("/dm ")) {
            response.put("command", "dm");
            String targetUserName = getUserNamesFromMessage(currentCommand).get(0);
            User targetUser = userService.getUserByName(targetUserName);
            if (targetUser != null) {
                response.put("status", "OK");
                response.put("report", sendDirectMessage(command.getUserId(), targetUser,
                        currentCommand.substring(currentCommand.indexOf(targetUserName) + targetUserName.length()), command.getChannelId()));
                response.put("targetUserId", targetUser.getId().toString());
                response.put("conversationId", conversationService.getConversationByUsers(command.getUserId(), targetUser.getId()).getId().toString());
            } else {
                response.put("status", "ERROR");
                response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(), "User @" + targetUserName + " not found"));
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
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageDtoService.toDto(newMessage));
    }

    private String inviteUsersToChannel(List<User> invitedUsers, Channel targetChannel, Long inviterId) throws JsonProcessingException {
        List<User> newUsersInChannel = new ArrayList<>(); //список новый пользователей
        List<User> existUsers = new ArrayList<>(); //список уже существующих в канале пользователей
        Workspace ws = targetChannel.getWorkspace();
        invitedUsers.forEach(user -> {
            if (targetChannel.getUsers().contains(user) || !ws.getUsers().contains(user)) {
                existUsers.add(user);
            } else {
                targetChannel.getUsers().add(user);
                newUsersInChannel.add(user);
            }
        });
        channelService.updateChannel(targetChannel);
        User invitingUser = userService.getUserById(inviterId);
        StringBuffer sb = new StringBuffer("Invite ");
        newUsersInChannel.forEach(user -> sb.append("@").append(user.getName()).append(" "));
        sb.append("to this channel");
        invitedUsers.removeAll(existUsers); //убираем существующих пользователей, оставляем только вновь приглашенных.
        return sendRequestMessage(targetChannel.getId(), invitingUser, sb.toString());
    }

    private String whoAreInChannel(Channel targetChannel, Long targetUserId) throws JsonProcessingException {
        StringBuffer sb = new StringBuffer("Users here:");
        Set<User> usersOnChannel = targetChannel.getUsers();
        User targetUser = userService.getUserById(targetUserId);
        boolean targetUserIsOnChannel = usersOnChannel.contains(targetUser);
        if (usersOnChannel.size() == 0) {
            sb.append(" hmmm...Nobody? wtf");
        } else if (usersOnChannel.size() == 1 && targetUserIsOnChannel) {
            sb.append(" just you");
        } else {
            targetChannel.getUsers().forEach(user -> {
                if (!user.equals(targetUser)) {
                    sb.append(" @").append(user.getName());
                }
            });
            sb.append(targetUserIsOnChannel ? " and you." : ".");
        }
        return sendTempRequestMessage(targetChannel.getId(), getBot(), sb.toString());
    }

    private List<String> getUserNamesFromMessage(String msg) {
        List<String> usersNames = new ArrayList<>();
        Arrays.asList(msg.replaceAll("\\s+", " ").split(" ")).forEach(word -> {
            if (word.startsWith("@")) {
                usersNames.add(word.substring(1));
            }
        });
        if (usersNames.size() == 0) {
            usersNames.add("");
        }
        return usersNames;
    }

    private List<String> getChannelsNamesFromSmg(String msg) {
        List<String> channelsNames = new ArrayList<>();
        Arrays.asList(msg.replaceAll("\\s+", " ").split(" ")).forEach(word -> {
            if (word.startsWith("#")) {
                channelsNames.add(word.substring(1));
            }
        });
        if (channelsNames.size() == 0) {
            channelsNames.add("");
        }
        return channelsNames;
    }

    private String kickUsers(List<User> usersToKick, Channel targetChannel, Long userId) throws JsonProcessingException {
        User currentUser = userService.getUserById(userId);
        targetChannel.getUsers().removeAll(usersToKick);
        channelService.updateChannel(targetChannel);
        StringBuffer msg = new StringBuffer("Users:");
        usersToKick.forEach(user -> msg.append(" @").append(user.getName()));
        msg.append(usersToKick.size() > 1 ? " were" : " was");
        msg.append(" kicked from channel");
        msg.append(" by @").append(currentUser.getName());
        return sendRequestMessage(targetChannel.getId(), getBot(), msg.toString());
    }











}
