package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class GoogleDriveSlashCommandImpl implements GoogleDriveSlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(
            GoogleDriveSlashCommandImpl.class);

    private UserService userService;
    private ChannelService channelService;
    private Bot bot;
    private BotService botService;
    private MessageService messageService;
    private GoogleDriveService googleDriveService;

    private final String INCORRECT_COMMAND = "Command is incorrect";
    private String status;

    public GoogleDriveSlashCommandImpl(UserService userService,
                                       ChannelService channelService,
                                       BotService botService, MessageService messageService,
                                       GoogleDriveService googleDriveService) {
        this.userService = userService;
        this.channelService = channelService;
        this.botService = botService;
        this.messageService = messageService;
        this.googleDriveService = googleDriveService;
    }

    @Override
    public String getCommand(SlashCommandDto command) throws JsonProcessingException {
        User currentUser = userService.getUserById(command.getUserId());
        String tokenCurrentUser = currentUser.getGoogleDriveToken();
        Channel currentChannel = channelService.getChannelById(command.getChannelId());
        String commandName = command.getName();

        String commandBody = command.getCommand().substring(command.getCommand().indexOf(commandName) + commandName.length()).trim();

        Map<String, String> response = new HashMap<>();
        response.put("userId", command.getUserId().toString());
        response.put("command", commandName);
        response.put("channelId", command.getChannelId().toString());
        response.put("report", "{}");

        ObjectMapper mapper = new ObjectMapper();

        response = createReport(command, currentUser, currentChannel, commandName, commandBody, response, mapper, tokenCurrentUser);

        return mapper.writeValueAsString(response);
    }

    private Map<String, String> createReport(SlashCommandDto command, User currentUser, Channel currentChannel,
                                             String commandName, String commandBody, Map<String, String> response,
                                             ObjectMapper mapper, String token) throws JsonProcessingException {
        switch (commandName) {
            case "google_drive_create_folder":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                    logger.info("Тело slash команды пустое");
                }
                else {
                    status = googleDriveService.addFolder(commandBody, token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "new Folder is created"));
                        logger.info("Создана папка с именем: {}",commandBody);
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                        logger.warn("Создание папки не получилось. Ошибка: {}",status);
                    }
                }
                break;
        }

        return response;
    }

    private String sendTempRequestMessage(Long channelId, Object author, String reportMsg) throws JsonProcessingException {
        return createMessage(channelId, author, reportMsg, false);
    }

    private Bot getBot(Long botId) {
        if (bot == null) {
            bot = botService.getBotById(botId);
        }
        return bot;
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
        newMessage.setWorkspace(channelService.getWorkspaceByChannelId(channelId));
        if (saveToBase) {
            messageService.createMessage(newMessage);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageService.getMessageDtoByMessage(newMessage));
    }


}
