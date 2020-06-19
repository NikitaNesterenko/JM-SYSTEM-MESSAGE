package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
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
    private final String INCORRECT_COMMAND = "Command is incorrect";
    private final UserService userService;
    private final ChannelService channelService;
    private final BotService botService;
    private final MessageService messageService;
    private final GoogleDriveService googleDriveService;
    private Bot bot;
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
        String commandName = command.getName();

        String commandBody = command.getCommand().substring(command.getCommand().indexOf(commandName) + commandName.length()).trim();

        Map<String, String> response = new HashMap<>();
        response.put("userId", command.getUserId().toString());
        response.put("command", commandName);
        response.put("channelId", command.getChannelId().toString());
        response.put("report", "{}");

        ObjectMapper mapper = new ObjectMapper();

        response = createReport(command, commandName, commandBody, response, tokenCurrentUser);

        return mapper.writeValueAsString(response);
    }

    private Map<String, String> createReport(SlashCommandDto command, String commandName, String commandBody, Map<String, String> response,
                                             String token) throws JsonProcessingException {
        switch (commandName) {
            case "google_drive_create_folder":
                if (commandBody.trim().isEmpty()) {
                    createResponce(command, response, "ERROR", INCORRECT_COMMAND);
                    logger.info("Тело slash команды пустое");
                } else {
                    status = googleDriveService.addFolder(commandBody, token);
                    if (status.equals("OK")) {
                        createResponce(command, response, "OK", "new Folder is created");
                        logger.info("Создана папка с именем: {}", commandBody);
                    } else {
                        createResponce(command, response, status, status);
                        logger.warn("Создание папки не получилось. Ошибка: {}", status);
                    }
                }
                break;
            case "google_drive_upload_file":
                status = googleDriveService.uploadFile(token);
                if (status.equals("OK")) {
                    createResponce(command, response, "OK", "new file upload");
                    logger.info("Файл загружен");
                } else {
                    createResponce(command, response, status, status);
                    logger.warn("Создание папки не получилось. Ошибка: {}", status);
                }
                break;
        }
        return response;
    }

    private void createResponce(SlashCommandDto command, Map<String, String> response, String status, String status2) throws JsonProcessingException {
        response.put("status", status);
        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                status2));
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
