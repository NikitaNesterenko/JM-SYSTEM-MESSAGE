package jm;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TrelloServiceImpl implements TrelloService {
    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private UserService userService;
    private BotService botService;
    private WorkspaceService workspaceService;
    private SlashCommandService slashCommandService;
    private TypeSlashCommandService typeSlashCommandService;
    @Value("${trello.api-key}")
    private String API_KEY;
    /* Тестовый токен: действует только 30 дней. При необходимости можно увеличить срок
    /  Тестовый API-Key: f1184d87df3d841e491cecffeb568165
    /  Информация по интеграции Trello: https://developer.atlassian.com/cloud/trello/     */

    public TrelloServiceImpl(RestTemplateBuilder builder, UserService userService, BotService botService,
                             WorkspaceService workspaceService, SlashCommandService slash, TypeSlashCommandService tsc) {
        this.userService = userService;
        restTemplate = builder.build();
        this.botService = botService;
        this.workspaceService = workspaceService;
        this.slashCommandService = slash;
        this.typeSlashCommandService = tsc;
    }

    @Override
    public void setToken (String token) {
        //todo Проверка на наличие токена
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByLogin(userDetails.getUsername());
        user.setTrelloToken(token);
        userService.updateUser(user);
        setNewTrelloBot(token, user.getId());
    }

    public void setNewTrelloBot(String token, long id) {
        Bot trelloBot = new Bot();
        TypeSlashCommand tsc = new TypeSlashCommand();

        //todo проверка на наличие бота и слеш команд
        trelloBot.setName("Trello");
        trelloBot.setNickName("Trello");
        trelloBot.setCommands(trelloSlashCommand());
        trelloBot.setDateCreate(LocalDateTime.now());
        trelloBot.setIsDefault(true);
        trelloBot.setWorkspaces(new HashSet(workspaceService.getAllWorkspaces()));
        trelloBot.getCommands().forEach(slashCommand -> slashCommandService.simplePersist(slashCommand));
        botService.createBot(trelloBot);

        tsc.setName("Trello");
        typeSlashCommandService.createTypeSlashCommand(tsc);
        trelloBot.getCommands().forEach(slashCommand -> {
            slashCommand.setBot(botService.getBotBySlashCommandId(slashCommand.getId()));
            slashCommand.setType(tsc);
            slashCommandService.simpleMerge(slashCommand);
        });

    }

    public Set<SlashCommand> trelloSlashCommand() {
        Set<SlashCommand> slashCommands = new HashSet<>();
        slashCommands.add(new SlashCommand("trello_create_new_board","/app/bot/trello",
                                        "createNewBoard","createNewBoard"));
        slashCommands.add(new SlashCommand("trello_delete_board","/app/bot/trello",
                "delete board by id","delete board by id"));
        slashCommands.add(new SlashCommand("trello_get_action","/app/bot/trello",
                "get action","get action"));  //Don't work. I don't know, what is action
        slashCommands.add(new SlashCommand("trello_get_board","/app/bot/trello",
                "get board","get board"));
        slashCommands.add(new SlashCommand("trello_update_board_name","/app/bot/trello",
                "Update name of board","Update board name"));
        slashCommands.add(new SlashCommand("trello_create_new_list","/app/bot/trello",
                "Create new List","Create new List"));
        slashCommands.add(new SlashCommand("trello_update_list_name","/app/bot/trello",
                "trello_update_list_name","trello_update_list_name"));
        slashCommands.add(new SlashCommand("trello_get_list_byBoardId","/app/bot/trello",
                "trello_get_list","trello_get_list"));
        slashCommands.add(new SlashCommand("trello_create_new_card","/app/bot/trello",
                "create new card","need id List"));
        slashCommands.add(new SlashCommand("trello_get_card","/app/bot/trello",
                "get card by Id","need id Card"));
        slashCommands.add(new SlashCommand("trello_delete_card","/app/bot/trello",
                "delete card by Id","need id Card"));
        slashCommands.add(new SlashCommand("trello_add_comment_to_a_card","/app/bot/trello",
                "add comment to Card","need id Card and field text"));
        return slashCommands;
    }

    @Override
    public String getTokenByUserLogin () {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserByLogin(userDetails.getUsername()).getTrelloToken();
    }

    @Override
    public String getTokenByUserName (String userName) {
        return userService.getUserByName(userName).getTrelloToken();
    }

    @Override
    public String getBoardOrCardByURL(String URL, String token) {
        return restTemplate.getForEntity(URL + ".json?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getBoardOrCardByName (String pointer, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/search?query=" + pointer + "&key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getListsByBoardID (String boardID, String token) {
        try{
            String cardString = restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID
                    + "/lists?key=" + API_KEY + "&token=" + token, String.class).getBody();
            JSONArray jsonBoard = new JSONArray(cardString);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i<jsonBoard.length(); i++){
                JSONObject o = jsonBoard.getJSONObject(i);
                sb.append("List name: ")
                        .append(o.get("name"))
                        .append("List id: ")
                        .append(o.get("id"))
                        .append("  |||   ");
            }
             return sb.toString();
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public String getCardsByListID (String listID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/lists/" + listID +
                "/cards" + "?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getCardByCardID (String cardID, String token) {
        String cardString = restTemplate.getForEntity("https://api.trello.com/1/cards/" + cardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
        JSONObject jsonBoard = new JSONObject(cardString);
        return "Card id: " + jsonBoard.get("id") + "\n     Card name: " + jsonBoard.get("name");
    }

    @Override
    public String getBoardByBoardID (String boardID, String token) {
        String boardString = restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID + "?key="
                + API_KEY + "&token=" + token, String.class).getBody();
        JSONObject jsonBoard = new JSONObject(boardString);
        return "Board id: " + jsonBoard.get("id") + "\n     Board name: " + jsonBoard.get("name") +
                "\n     Board url:" + jsonBoard.get("url");
    }

    @Override
    public String addNewCard (String cardName, String listID, String token) {
       try{
           restTemplate.postForEntity("https://api.trello.com/1/cards?idList=" + listID
                   + "&name=" + cardName + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
        return "OK";
       } catch (HttpClientErrorException e) {
           return e.getMessage();
       }
    }

    @Override
    public String createList (String BoardID, String listName, String token) {
      try{
          restTemplate.postForEntity("https://api.trello.com/1/lists?name=" + listName
                  + "&idBoard=" + BoardID + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
      return "OK";
      } catch (HttpClientErrorException e) {
          return e.getMessage();
      }
    }

    @Override
    public String addCommentToCard (String comment, String cardID, String token) {
        try{
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/actions/comments?text=" + comment
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public void setCardDueDate (String RFC822date, String cardID, String cardJSON, String token) {
        restTemplate.put("https://api.trello.com/1/cards/" + cardID
                + "?due=" + RFC822date + "&key=" + API_KEY + "&token=" + token, cardJSON);
    }

    @Override
    public String getUserIDByUserToken (String token) {
        String userJSON = restTemplate.getForEntity("https://api.trello.com/1/tokens/" + token + "/member?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
        String userID = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            userID = objectMapper.readTree(userJSON).get("id").asText();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error occurred while trying get user ID from user JSON!");
        }

        return userID;
    }

    @Override
    public String deleteBoard(String boardID, String userToken) {
        try {
         restTemplate.delete("https://api.trello.com/1/boards/" + boardID + "?key="
                + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public String updateBoardName(String boardID, String newName, String userToken) {
        try {
            restTemplate.put("https://api.trello.com/1/boards/" + boardID + "?name=" + newName + "&key="
                    + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

        @Override
        public String updateListName(String listID, String newName, String userToken) {
            try {
                restTemplate.put("https://api.trello.com/1/lists/" + listID + "?name=" + newName + "&key="
                        + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
                return "OK";
            }catch (HttpClientErrorException e) {
                return e.getMessage();
            }
        }

    @Override
    public String deleteCard(String cardID, String userToken) {
        try {
            restTemplate.delete("https://api.trello.com/1/cards/" + cardID + "?key="
                    + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public String addBoard(String boardName, String token) {
        try{
            restTemplate.postForEntity("https://api.trello.com/1/boards/?name=" + boardName + "&key="
                    + API_KEY + "&token=" + token, new HttpHeaders(), String.class).getStatusCodeValue();
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public String getAction(String id, String token) {
        try{
            restTemplate.getForEntity("https://api.trello.com/1/actions/" + id + "?key="
                    + API_KEY + "&token=" + token, String.class);
            return "OK";
        } catch (HttpClientErrorException e) {
            return e.getMessage();
        }
    }

    @Override
    public void assignUserToCardByUserID (String userID, String cardID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/idMembers?value=" + userID
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    @Override
    public String formatStringToRFC822Date(String week, int day, int hour) {
        if (!week.equals("this") && !week.equals("next")) {
            return null;
        }
        if (day < 1 || day > 7) {
            return null;
        }
        if (hour < 1 || hour > 24) {
            return null;
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(currentDate);
        if (week.equals("next")) {
            calendar.add(Calendar.DATE, 7);
        }
        if (day == 7) {
            day = 0;
        }
        calendar.set(Calendar.DAY_OF_WEEK, day + 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentDate = calendar.getTime();

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(currentDate);
    }
}
