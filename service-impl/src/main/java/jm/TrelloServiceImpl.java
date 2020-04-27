package jm;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class TrelloServiceImpl {
    private final String API_KEY = "606bf0409dd3c5dcd9bc40e2430e237e";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;

    @Autowired
    public void setUserService (UserService userService) {
        this.userService = userService;
    }

    public void setToken (String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByLogin(userDetails.getUsername());
        user.setTrelloToken(token);
        userService.updateUser(user);
    }

    public String getTokenByUserLogin () {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserByLogin(userDetails.getUsername()).getTrelloToken();
    }

    public String getTokenByUserName (String userName) {
        return userService.getUserByName(userName).getTrelloToken();
    }

    public String getBoardOrCardByURL(String URL, String token) {
        String objectJSON = null;

        try {
            objectJSON = restTemplate.getForEntity(URL + ".json?key=" + API_KEY + "&token=" + token, String.class).getBody();
        } catch (IllegalArgumentException ex) {

        }

        return objectJSON;
    }

    public String getBoardOrCardByName (String pointer, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/search?query=" + pointer + "&key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    public String getListsByBoardID (String boardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID
                + "/lists?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    public String getCardsByListID (String listID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/lists/" + listID +
                "/cards" + "?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    public String getCardByCardID (String cardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/cards/" + cardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    public String getBoardByBoardID (String boardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    public void addNewCard (String cardName, String listID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards?idList=" + listID
                + "&name=" + cardName + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    public void addCommentToCard (String comment, String cardID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/actions/comments?text=" + comment
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    public void setCardDueDate (String RFC822date, String cardID, String cardJSON, String token) {
        restTemplate.put("https://api.trello.com/1/cards/" + cardID
                + "?due=" + RFC822date + "&dueComplete=false&key=" + API_KEY + "&token=" + token, cardJSON);
    }

    public String getUserIDByUserToken (String token) {
        String userJSON = restTemplate.getForEntity("https://api.trello.com/1/tokens/" + token + "/member?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
        String userID = null;

        try {
            userID = objectMapper.readTree(userJSON).get("id").asText();
        } catch (IOException ex) {

        }

        return userID;
    }

    public void assignUserToCardByUserID (String userID, String cardID, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/idMembers?value=" + userID
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    public String formatStringToRFC822Date(String date) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        boolean isDateFormatted = false;

        if (date.equalsIgnoreCase("Tomorrow") || date.equalsIgnoreCase("Завтра")) {
            calendar.setTime(currentDate);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            currentDate = calendar.getTime();
            isDateFormatted = true;
        }

        // Нужно добавить обработку для ближайшей пятницы пятницы, следующей недели, а также точного указания даты.

        if (isDateFormatted) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(timeZone);
            return dateFormat.format(currentDate);
        }

        return null;
    }
}
