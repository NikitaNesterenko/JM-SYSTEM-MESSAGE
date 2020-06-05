package jm;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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
    @Value("${trello.api-key}")
    private String API_KEY;
    /* Тестовый токен: 247b4449072f5204331931ced057e48d3188e589956accec21e28054904a7543
    /  Тестовый API-Key: f1184d87df3d841e491cecffeb568165
    /  Информация по интеграции Trello: https://developer.atlassian.com/cloud/trello/     */

    public TrelloServiceImpl(RestTemplateBuilder builder, UserService userService,
                             BotService botService,WorkspaceService workspaceService) {
        this.userService = userService;
        restTemplate = builder.build();
        this.botService = botService;
        this.workspaceService = workspaceService;
    }

    @Override
    public void setToken (String token) {
        //todo Проверка на наличие токена
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByLogin(userDetails.getUsername());
        user.setTrelloToken(token);
        userService.updateUser(user);
        setNewTrelloApp(token, user.getId());
    }

    public void setNewTrelloApp(String token, long id) {
        App trello = new App();
        trello.setName("Trello");
        trello.setToken(token);
        trello.setClientId(String.valueOf(id));
        trello.setWorkspace(workspaceService.getWorkspaceById(1L));  //todo Тестовый вариант, исправить
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
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID
                + "/lists?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getCardsByListID (String listID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/lists/" + listID +
                "/cards" + "?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getCardByCardID (String cardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/cards/" + cardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    @Override
    public String getBoardByBoardID (String boardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    @Override
    public void addNewCard (String cardName, String listID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards?idList=" + listID
                + "&name=" + cardName + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    @Override
    public void addCommentToCard (String comment, String cardID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/actions/comments?text=" + comment
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
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
    public void deleteBoard(String boardID, String userToken) {
        restTemplate.delete("https://api.trello.com/1/boards/" + boardID + "?key="
                + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
    }

    @Override
    public void deleteCard(String cardID, String userToken) {
        restTemplate.delete("https://api.trello.com/1/boards/" + cardID + "?key="
                + API_KEY + "&token=" + userToken, new HttpHeaders(), String.class);
    }

    @Override
    public void addBoard(String boardName, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/boards/" + boardName + "?key="
                + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
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
