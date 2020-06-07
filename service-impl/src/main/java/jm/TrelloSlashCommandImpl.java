package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class TrelloSlashCommandImpl implements TrelloSlashCommand {

    private UserService userService;
    private ChannelService channelService;
    private TrelloService trelloService;
    private MessageService messageService;
    private SlashCommandService slashCommandService;
    private BotService botService;

    private final String INCORRECT_COMMAND = "Command is incorrect";
    private final String COMMAND_DENIED = "Command only for ";

    private Bot bot;
    private String status;

    public TrelloSlashCommandImpl(UserService userService, ChannelService channelService, TrelloService trelloService,
                                  MessageService messageService, SlashCommandService slashCommandService, BotService botService) {
        this.userService = userService;
        this.channelService = channelService;
        this.trelloService = trelloService;
        this.messageService = messageService;
        this.slashCommandService = slashCommandService;
        this.botService = botService;
    }

    @Override
    public String getCommand(SlashCommandDto command) throws JsonProcessingException {
        User currentUser = userService.getUserById(command.getUserId());//пользователь, отправивший команду
        String token = currentUser.getTrelloToken();
        Channel currentChannel = channelService.getChannelById(command.getChannelId());  //канал, куда отправлена команда
        String commandName = command.getName();  //название пришедшей команды
        //тело команды (все что после commandName)
        String commandBody = command.getCommand().substring(command.getCommand().indexOf(commandName) + commandName.length()).trim();
        Map<String, String> response = new HashMap<>();
        response.put("userId", command.getUserId().toString()); //Id пользователя, отправившего запрос
        response.put("command", commandName); //добавляем название команды в ответ
        response.put("channelId", command.getChannelId().toString()); //добавляем Id канала, в котором отправлена команда
        response.put("report", "{}");

        ObjectMapper mapper = new ObjectMapper();

        response = createReport(command, currentUser, currentChannel, commandName, commandBody, response, mapper, token);

        return mapper.writeValueAsString(response);
    }

    private Map<String, String> createReport(SlashCommandDto command, User currentUser, Channel currentChannel,
                                             String commandName, String commandBody, Map<String, String> response,
                                             ObjectMapper mapper, String token) throws JsonProcessingException {
        switch (commandName) {
            case "trello_create_new_board":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    status = trelloService.addBoard(commandBody,token);
                    if (status.equals("OK")){
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "new Board is created"));
                    } else {
                        response.put("status",status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_delete_board":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                   status = trelloService.deleteBoard(commandBody, token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Board was success delete"));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_get_action":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    status = trelloService.getAction(commandBody,token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Action success create"));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_get_board":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String board = trelloService.getBoardByBoardID(commandBody,token);
                    if (!board.isEmpty()) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                board));
                    } else {
                        response.put("status", "ERROR");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Error"));
                    }
                }
                break;
            case "trello_update_board_name":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String[] str = commandBody.split(" ");
                    String id = str[0];
                    String name = str[1];
                    status = trelloService.updateBoardName(id,name,token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Board success updete, new name: " + name));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_create_new_list":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String[] str = commandBody.split(" ");
                    String name = str[0];
                    String id = str[1];
                    status = trelloService.createList(name,id,token);
                    if (status.equals("OK")){
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "new List "+id+" is created"));
                    } else {
                        response.put("status",status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_update_list_name":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String[] str = commandBody.split(" ");
                    String id = str[0];
                    String name = str[1];
                    status = trelloService.updateListName(id,name,token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Lists success update, new name: " + name));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_get_list_byBoardId":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String board = trelloService.getListsByBoardID(commandBody,token);
                    if (!board.isEmpty()) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                board));
                    } else {
                        response.put("status", "ERROR");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Error"));
                    }
                }
                break;
            case "trello_create_new_card":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String[] str = commandBody.split(" ");
                    String name = str[0];
                    String id = str[1];
                    status = trelloService.addNewCard(name,id,token);
                    if (status.equals("OK")){
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "new Card "+name+" is created"));
                    } else {
                        response.put("status",status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_get_card":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String card = trelloService.getCardByCardID(commandBody,token);
                    if (!card.isEmpty()) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                card));
                    } else {
                        response.put("status", "ERROR");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Error"));
                    }
                }
                break;
            case "trello_delete_card":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                   status = trelloService.deleteCard(commandBody, token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Card war success delete"));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
            case "trello_add_comment_to_a_card":
                if (commandBody.trim().isEmpty()) {
                    response.put("status", "ERROR");
                    response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()), INCORRECT_COMMAND));
                } else {
                    String[] str = commandBody.split(" ");
                    String id = str[0];
                    String comment = str[1];
                    status = trelloService.addCommentToCard(comment, id, token);
                    if (status.equals("OK")) {
                        response.put("status", "OK");
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                "Comment success add"));
                    } else {
                        response.put("status", status);
                        response.put("report", sendTempRequestMessage(command.getChannelId(), getBot(command.getBotId()),
                                status));
                    }
                }
                break;
        }



        return response;
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
        newMessage.setWorkspace(channelService.getWorkspaceByChannelId(channelId));
        if (saveToBase) {
            messageService.createMessage(newMessage);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageService.getMessageDtoByMessage(newMessage));
    }

    private Bot getBot(Long botId) {
        if (bot == null) {
            bot = botService.getBotById(botId);
        }
        return bot;
    }

}
