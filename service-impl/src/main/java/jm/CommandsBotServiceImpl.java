package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jm.dto.ChannelDtoService;
import jm.dto.DirectMessageDtoService;
import jm.dto.MessageDtoService;
import jm.dto.SlashCommandDto;
import jm.model.*;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommandsBotServiceImpl implements CommandsBotService {
    private Logger logger = LoggerFactory.getLogger(CommandsBotServiceImpl.class);
    private BotService botService;
    private ChannelDtoService channelDtoService;
    private ChannelService channelService;
    private ConversationService conversationService;
    private DirectMessageDtoService directMessageDtoService;
    private DirectMessageService directMessageService;
    private MessageDtoService messageDtoService;
    private MessageService messageService;
    private SlashCommandService slashCommandService;
    private UserService userService;

    private final String INCORRECT_COMMAND = "Command is incorrect";

    private Bot bot;

    @Autowired
    public CommandsBotServiceImpl(BotService botService, ChannelDtoService channelDtoService, ChannelService channelService,
                                  ConversationService conversationService, DirectMessageDtoService directMessageDtoService,
                                  DirectMessageService directMessageService, MessageDtoService messageDtoService,
                                  MessageService messageService, SlashCommandService slashCommandService, UserService userService) {
        this.botService = botService;
        this.channelDtoService = channelDtoService;
        this.channelService = channelService;
        this.conversationService = conversationService;
        this.directMessageDtoService = directMessageDtoService;
        this.directMessageService = directMessageService;
        this.messageDtoService = messageDtoService;
        this.messageService = messageService;
        this.slashCommandService = slashCommandService;
        this.userService = userService;
    }

    @Override
    public String getWsCommand(SlashCommandDto command) throws JsonProcessingException {
        User currentUser = userService.getUserById(command.getUserId());//пользователь, отправивший команду
        Channel currentChannel = channelService.getChannelById(command.getChannelId()); //канал, куда отправлена команда
        String commandName = command.getName(); //название пришедшей команды
        //тело команды (все что после commandName)
        String commandBody = command.getCommand().substring(command.getCommand().indexOf(commandName) + commandName.length()).trim();
        Map<String, String> response = new HashMap<>();
        response.put("userId", command.getUserId().toString()); //Id пользователя, отправившего запрос
        response.put("command", commandName); //добавляем название команды в ответ
        response.put("channelId", command.getChannelId().toString()); //добавляем Id канала, в котором отправлена команда
        response.put("report", "{}");

        ObjectMapper mapper = new ObjectMapper();
        switch (commandName) {
            case "topic":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    setTopic(command.getChannelId(), commandBody);
                    response.put("topic", commandBody);
                    response.put("status", "OK");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), "Topic was changed"));
                }
                break;
            case "leave": {
                String channelName = getChannelsNamesFromMsg(commandBody).size() > 1 ? "" :
                        getChannelsNamesFromMsg(commandBody).get(0);
                Channel channel = channelService.getChannelByName(channelName);
                if (channel == null) {
                    if (commandBody.equals("")) {
                        channel = channelService.getChannelById(command.getChannelId());
                        response.put("report", leaveChannel(channel, command.getUserId()));
                        response.put("status", "OK");
                        response.put("targetChannelId", channel.getId().toString());
                    } else {
                        response.put("status", "ERROR");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                    }
                } else {
                    response.put("report", leaveChannel(channel, command.getUserId()));
                    response.put("status", "OK");
                    response.put("targetChannelId", channel.getId().toString());
                }
                break;
            }
            case "join":
            case "open": {
                String channelName = getChannelsNamesFromMsg(commandBody).size() > 1 ? "" :
                        getChannelsNamesFromMsg(commandBody).get(0);
                Channel channel = channelService.getChannelByName(channelName);
                if (channel != null) {
                    response.put("report", joinChannel(channel, command.getUserId()));
                    response.put("status", "OK");
                    response.put("channel", mapper.writeValueAsString(channelDtoService.toDto(channel)));
                    response.put("targetChannelId", channel.getId().toString());
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                }

                break;
            }
            case "shrug":
                response.put("report", sendPermRequestMessage(command.getChannelId(),
                        userService.getUserById(command.getUserId()), commandBody + " ¯\\_(ツ)_/¯"));
                response.put("status", "OK");
                break;
            case "invite":
                List<User> invitedUsers = getUsersFromMessage(commandBody); //список пользователей, которых приглашаем

                List<String> channelsName = getChannelsNamesFromMsg(commandBody); //список каналов, куда приглашаем, выбираем только первый (пока что?)

                Channel channelToInvite = channelService.getChannelByName(channelsName.get(0));

                //если канал не указан, то выбираем канал, в который отправлена команда, иначе выбираем первый упомянутый канал
                if (channelToInvite == null && channelsName.get(0).equals("")) {
                    channelToInvite = channelService.getChannelById(command.getChannelId());
                }
                //убираем всех существующих на канале пользователей
                if (channelToInvite != null) {
                    invitedUsers.removeAll(channelToInvite.getUsers());
                }
                invitedUsers.remove(null);
                //если список пользователей пуст или указанный канал не найден отправляем ошибку
                if (channelToInvite != null && !invitedUsers.isEmpty()) {
                    response.put("report", inviteUsersToChannel(invitedUsers, channelToInvite, command.getUserId()));
                    response.put("status", "OK");
                    List<Long> finalUserIds = new ArrayList<>();
                    invitedUsers.forEach(user -> finalUserIds.add(user.getId()));
                    response.put("targets", mapper.writeValueAsString(finalUserIds)); //id добавленный пользователей
                    response.put("channel", mapper.writeValueAsString(channelToInvite)); //канал, куда добавляли пользователей
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), channelToInvite == null ?
                            "Channel not found" : "Users list is empty or all users are already in channel"));
                }
                break;
            case "who":
                if (commandBody.replaceAll("\\s+", "").trim().equals("")) { //проверяем, есть ли текст после /who
                    response.put("report", whoAreInChannel(currentChannel, command.getUserId(), command.getBotId()));
                    response.put("status", "OK");
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                }
                break;
            case "remove":
            case "kick":
                List<User> kickedUser = new ArrayList<>();
                getUsersFromMessage(commandBody).forEach(user -> {
                    if (currentChannel.getUsers().contains(user)) {
                        kickedUser.add(user);
                    }
                });
                if (kickedUser.size() > 0) {
                    response.put("status", "OK");
                    response.put("report", kickUsers(kickedUser, currentChannel, command.getUserId(), command.getBotId()));
                    response.put("kickedUsersIds", mapper.writeValueAsString(kickedUser.stream().map(User::getId).toArray()));
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(currentChannel.getId(), getBot(command.getBotId()), "Users not found"));
                }
                break;
            case "msg":
                String targetChannelName = getChannelsNamesFromMsg(commandBody).get(0);
                Channel targetChannel = channelService.getChannelByName(targetChannelName);
                if (targetChannel != null) {
                    response.put("status", "OK");
                    response.put("report", sendPermRequestMessage(targetChannel.getId(), userService.getUserById(command.getUserId()),
                            commandBody.substring(commandBody.indexOf(targetChannelName) + targetChannelName.length())));
                    response.put("targetChannelId", targetChannel.getId().toString());
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                }
                break;
            case "dm":
                User targetUser = getUsersFromMessage(commandBody).get(0);
                String targetUserName = targetUser.getName();
                if (targetUser != null) {
                    response.put("status", "OK");
                    response.put("report", sendDirectMessage(command.getUserId(), targetUser,
                            commandBody.substring(commandBody.indexOf(targetUserName) + targetUserName.length()), command.getChannelId()));
                    response.put("targetUserId", targetUser.getId().toString());
                    response.put("conversationId", conversationService.getConversationByUsersId(command.getUserId(), targetUser.getId()).getId().toString());
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), "User @" + targetUserName + " not found"));
                }
                break;
            case "rename":
                Channel channelToRename = channelService.getChannelById(command.getChannelId());
                if (channelToRename != null && !commandBody.replaceAll("\\s+", " ").trim().equals("")) {
                    response.put("status", "OK");
                    response.put("report", renameChannel(commandBody, channelToRename, currentUser));
                    response.put("newChannelName", commandBody);
                    response.put("targetChannelId", channelToRename.getId().toString());
                } else {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                }
                break;
            case "archive":
                response.put("status", "OK");
                response.put("report", archiveChannel(currentChannel, currentUser));
                break;
            case "invite_people":
                //boolean isEMailFirst;
                List<String> emailsList = new ArrayList<>();
                Arrays.asList(commandBody.replaceAll("\\s+", " ").split(" ")).forEach(word -> {
                    if (word.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
                        emailsList.add(word);
                    }
                });
                if (emailsList.size() == 0) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), "Emails not found"));
                } else {
                    response.put("status", "OK");
                    response.put("usersList", mapper.writeValueAsString(emailsList));
                }
                break;
        }
        return mapper.writeValueAsString(response);
    }

    @Override
    public String sendMsg(SlashCommandDto command) throws JsonProcessingException {
        String msg = slashCommandService.getSlashCommandByName(command.getName()).getHints();
        return sendPermRequestMessage(command.getChannelId(), getBot(command.getBotId()), msg);
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
        return sendPermRequestMessage(channel.getId(), user, "Was left the channel");
    }

    private String sendDirectMessage(Long fromId, User toUser, String message, Long channelId) throws JsonProcessingException {

        Channel channel = channelService.getChannelById(channelId);
        Workspace workspace = channel.getWorkspace();

        //Получаем conversation двух пользователей, если существовала
        Conversation conv = conversationService.getConversationByUsersId(fromId, toUser.getId());
        //создаем новый conversation для пользователей
        if (conv == null) {
            conv = new Conversation();
            conv.setOpeningUser(userService.getUserById(fromId));
            conv.setWorkspace(workspace);
            conv.setAssociatedUser(toUser);
            conv.setShowForAssociated(true);
            conv.setShowForOpener(true);
            conversationService.createConversation(conv);
            conv = conversationService.getConversationByUsersId(fromId, toUser.getId());
        }

        //создаем DirectMessage
        DirectMessage dm = new DirectMessage();
        dm.setConversation(conv);
        dm.setDateCreate(LocalDateTime.now());
        dm.setUser(userService.getUserById(fromId));
        dm.setContent(message);
        dm.setIsDeleted(false);
        dm.setRecipientUsers(new HashSet<>());
        directMessageService.saveDirectMessage(dm);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(directMessageDtoService.toDto(dm));
    }

    private String joinChannel(Channel channel, Long userId) throws JsonProcessingException {
        User user = userService.getUserById(userId);
        if (!channel.getUsers().contains(user)) {
            channel.getUsers().add(user);
            channelService.updateChannel(channel);
            return sendPermRequestMessage(channel.getId(), user, "Joined to channel");
        }
        return null;
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
        return sendPermRequestMessage(targetChannel.getId(), invitingUser, sb.toString());
    }

    private String whoAreInChannel(Channel targetChannel, Long targetUserId, Long botId) throws JsonProcessingException {
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
        return sendTempRequestMessage(targetChannel.getId(), getBot(botId), sb.toString());
    }

    private List<User> getUsersFromMessage(String msg) {
        List<User> usersNames = new ArrayList<>();
        Arrays.asList(msg.replaceAll("\\s+", " ").split(" ")).forEach(word -> {
            if (word.startsWith("@")) {
                usersNames.add(userService.getUserByName(word.substring(1)));
            }
        });
        if (usersNames.size() == 0) {
            usersNames.add(null);
        }
        return usersNames;
    }

    private List<String> getChannelsNamesFromMsg(String msg) {
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

    private String kickUsers(List<User> usersToKick, Channel targetChannel, Long userId, Long botId) throws JsonProcessingException {
        User currentUser = userService.getUserById(userId);
        targetChannel.getUsers().removeAll(usersToKick);
        channelService.updateChannel(targetChannel);
        StringBuffer msg = new StringBuffer("Users:");
        usersToKick.forEach(user -> msg.append(" @").append(user.getName()));
        msg.append(usersToKick.size() > 1 ? " were" : " was");
        msg.append(" kicked from channel");
        msg.append(" by @").append(currentUser.getName());
        return sendPermRequestMessage(targetChannel.getId(), getBot(botId), msg.toString());
    }

    private String renameChannel(String newChannelName, Channel targetChannel, User user) throws JsonProcessingException {
        String oldName = targetChannel.getName();
        targetChannel.setName(newChannelName);
        channelService.updateChannel(targetChannel);
        return sendPermRequestMessage(targetChannel.getId(), user, "renamed the channel from “" + oldName + "” to “" + newChannelName + "”");
    }

    private String archiveChannel(Channel targetChannel, User user) throws JsonProcessingException {
        targetChannel.setArchived(true);
        channelService.updateChannel(targetChannel);
        return sendPermRequestMessage(targetChannel.getId(), user, "archived #" + targetChannel.getName() + ". The contents will still be browsable.");
    }

    private String sendPermRequestMessage(Long channelId, Object author, String reportMsg) throws JsonProcessingException {
        return createMessage(channelId, author, reportMsg, true);
    }

    private String sendTempRequestMessage(Long channelId, Object author, String reportMsg) throws JsonProcessingException {
        return createMessage(channelId, author, reportMsg, false);
    }

    private String createMessage(Long channelId, Object author, String reportMsg, boolean saveToBase) throws JsonProcessingException {
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
        if (saveToBase) {
            messageService.createMessage(newMessage);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageDtoService.toDto(newMessage));
    }

    private Bot getBot(Long botId) {
        if (bot == null) {
            bot = botService.getBotById(botId);
        }
        return bot;
    }

}
