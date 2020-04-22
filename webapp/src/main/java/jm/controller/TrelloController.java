package jm.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.UserService;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/trello")
public class TrelloController {
    private final String API_KEY = "606bf0409dd3c5dcd9bc40e2430e237e";
    private String boardID;
    private String listID;
    private String cardID;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;

    @Autowired
    public void setUserService (UserService userService) {
        this.userService = userService;
    }

    /**
     * Метод устанавливает токен Trello для текущего авторизованного пользователя.
     *
     * @param token - токен Trello.
     * @param principal - данные текущего пользователя.
     */

    @PostMapping (value = "/token")
    public ResponseEntity <String> setUserToken (
            @RequestParam (name = "value") String token,
            @Autowired Principal principal) {

        User user = userService.getUserByLogin(principal.getName());
        user.setTrelloToken(token);
        userService.updateUser(user);

        return new ResponseEntity<> ("Trello token was set for current user!", HttpStatus.OK);
    }

    /**
     * Метод позволяет получить информацию о текущей доске.
     *
     * @param principal - данные текущего пользователя.
     */

    @GetMapping (value = "info", produces = "application/json")
    public ResponseEntity<String> getBoardInfo (
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);
            String json = getBoardByID(boardID, token);

            return new ResponseEntity<>(json, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Метод позволяет получить объект доски или карточки по ссылке на них. Также устанавливает текущую доску, если в
     * качестве параметра передано URL доски и доску/список/карточку, если в качестве параметра передано URL карточки.
     *
     * @param URL - ссылка на доску или карточку.
     * @param principal - данные текущего пользователя.
     */

    /*
    Slack не позволяет создавать карточки после установки текущей карточки через её URL и просит установить доску командой
    /trello link {board URL}. Отказался от этого решения, добавив установку доски/списка/карточки.
     */

    @GetMapping (value = "/details", produces = "application/json")
    public ResponseEntity<String> getBoardOrCardByURL (
            @RequestParam (name = "url") String URL,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            boolean isBoardURL = false;
            boolean isCardURL = false;
            if (URL.startsWith("https://trello.com/b/")) {
                isBoardURL = true;
            }
            if (URL.startsWith("https://trello.com/c/")) {
                isCardURL = true;
            }

            if (isBoardURL || isCardURL) {
                String json = getBoardOrCardByURL(URL, token);
                JsonNode jsonNode = objectMapper.readTree(json);

                if (isBoardURL) {
                    boardID = jsonNode.get("id").asText();
                }
                if (isCardURL) {
                    boardID = jsonNode.get("idBoard").asText();
                    listID = jsonNode.get("idList").asText();
                    cardID = jsonNode.get("id").asText();
                }

                return new ResponseEntity<>(json, HttpStatus.OK);
            }

            return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Метод позволяет получить объект доски по ссылке на неё. При поиске по имени возвращает все подходящие доски.
     * Устанавливает текущую доску, если была передана ссылка.
     *
     * @param pointer - указатель на доску. Может быть как ссылкой, так и именем.
     * @param principal - данные текущего пользователя.
     */

    @PostMapping (value = "/link", produces = "application/json")
    public ResponseEntity<String> getBoardByURL (
            @RequestParam (name = "pointer") String pointer,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            boolean isBoardURL = false;
            if (pointer.startsWith("https://trello.com/b/")) {
                isBoardURL = true;
            }

            if (isBoardURL) {
                String json = getBoardOrCardByURL(pointer, token);

                JsonNode jsonNode = objectMapper.readTree(json);
                boardID = jsonNode.get("id").asText();

                return new ResponseEntity<>(json, HttpStatus.OK);
            } else {
                String json = getBoardOrCardByName(pointer, token);
                JsonNode boardsNode = objectMapper.readTree(json).get("boards");

                if (boardsNode.isNull()) {
                    return new ResponseEntity<>("Cant find any boards!", HttpStatus.BAD_REQUEST);
                }

                StringBuilder boards = new StringBuilder();
                for (JsonNode boardNode : boardsNode) {
                    String boardID = boardNode.get("id").asText();
                    String boardJSON = getBoardByID(boardID, token);
                    boards.append(boardJSON);
                    boards.append(",");
                }
                boards.deleteCharAt(boards.length() - 1);
                
                return new ResponseEntity<>(boards.toString(), HttpStatus.OK);
            }
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Если передан параметр позиции списка на доске, то устанавливает текущий список. Если метод вызван без параметров,
     * то возвращает все списки.
     *
     * @param listPosition - позиция
     */

    @PostMapping (value = "/list", produces = "application/json")
    public ResponseEntity<String> getListIDFromBoard (
            @RequestParam (name = "pos") String listPosition,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            if (boardID == null) {
                return new ResponseEntity<>("Board not set!", HttpStatus.BAD_REQUEST);
            }

            String json = getListsByBoardID(token);
            JsonNode jsonNode = objectMapper.readTree(json);

            if (listPosition != null) {
                int listPos = Integer.parseInt(listPosition);
                int currentListPos = 1;
                for (JsonNode listNode : jsonNode) {
                    if (currentListPos == listPos) {
                        listID = listNode.get("id").asText();
                        return new ResponseEntity<>("List set!", HttpStatus.OK);
                    }
                    currentListPos++;
                }
            }

            return new ResponseEntity<>(json, HttpStatus.OK);
        }  catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        } catch (NumberFormatException ex) {
            return new ResponseEntity<>("Error in list position!", HttpStatus.BAD_REQUEST);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Метод создаёт новую карточку, делая её текущей.
     *
     * @param cardName - имя создаваемой карточки.
     */

    @PostMapping (value = "/add")
    public ResponseEntity<String> addCard (
            @RequestParam (name = "name") String cardName,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            if (listID == null) {
                return new ResponseEntity<>("List not set!", HttpStatus.BAD_REQUEST);
            }

            addNewCard(cardName, token);

            String json = getCardsByListID(token);
            JsonNode jsonNode = objectMapper.readTree(json);
            for (JsonNode cardNode : jsonNode) {
                String currentNodeCardName = cardNode.get("name").asText();
                if (cardName.equals(currentNodeCardName)) {
                    cardID = cardNode.get("id").asText();
                }
            }

            return new ResponseEntity<>("New card was created successfully!", HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Метод устанавливает срок истечения карточки для последней карточки.
     *
     * @param date
     */

    @PostMapping (value = "/due")
    public ResponseEntity<String> setDueDate (
            @RequestParam (name = "date") String date,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            if (cardID == null) {
                return new ResponseEntity<>("Card not set!", HttpStatus.BAD_REQUEST);
            }

            String RFC822Date = formatUserDateToRFC822Date(date);
            if (RFC822Date != null) {
                String json = getCardByID(cardID, token);
                setDueDate(RFC822Date, token, json);
                return new ResponseEntity<>("Due set!", HttpStatus.OK);
            }

            return new ResponseEntity<>("Error in date!", HttpStatus.BAD_REQUEST);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Метод добавляет комментарий к последней карточке.
     *
     * @param comment - текст комментария.
     * @param principal - данные текущего пользователя.
     */

    @PostMapping (value = "/comment")
    public ResponseEntity<String> addComment (
            @RequestParam (name = "text") String comment,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);

            if (cardID == null) {
                return new ResponseEntity<>("Card not set!", HttpStatus.BAD_REQUEST);
            }

            addCommentToCard(comment, token);

            return new ResponseEntity<>("Comment was added successfully!", HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Метод возвращает набор карточек, подходящих по запросу.
     *
     * @param name - имя искомой карточки.
     * @param principal - данные текущего пользователя.
     */

    @GetMapping (value = "/search", produces = "application/json")
    public ResponseEntity<String> performSearch (
            @RequestParam (name = "name") String name,
            @Autowired Principal principal) {

        try {
            String token = checkToken(principal);
            String json = getBoardOrCardByName(name, token);
            JsonNode cardsNode = objectMapper.readTree(json).get("cards");

            if (cardsNode.isNull()) {
                return new ResponseEntity<>("Cant find any cards!", HttpStatus.BAD_REQUEST);
            }

            StringBuilder cards = new StringBuilder();
            for (JsonNode cardNode : cardsNode) {
                if (!cardNode.get("closed").asBoolean()) {
                    cards.append(cardNode.toString());
                    cards.append(",");
                }
            }

            if (cards.length() != 0) {
                cards.deleteCharAt(cards.length() - 1);
                return new ResponseEntity<>(cards.toString(), HttpStatus.OK);
            }

            return new ResponseEntity<>("Cant find any cards!", HttpStatus.BAD_REQUEST);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>("Error in token!", HttpStatus.UNAUTHORIZED);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     *
     * @param memberID
     * @param principal
     */

    @PostMapping (value = "assign")
    public ResponseEntity<String> assignMember (
            @RequestParam (name = "id") String memberID,
            @Autowired Principal principal) {

        //https://api.trello.com/1/cards/{id}/idMembers=
        return null;
    }

    /*
    ===============================================================================================
    ================================= Private methods starts here =================================
    ===============================================================================================
     */

    private String checkToken (Principal principal) throws NullPointerException {
        String token = userService.getUserByLogin(principal.getName()).getTrelloToken();
        if (token == null) {
            throw new NullPointerException("Token not set!");
        }
        return token;
    }

    private String getBoardOrCardByURL(String URL, String token) {
        return restTemplate.getForEntity(URL + ".json?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    private String getBoardOrCardByName (String name, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/search?query=" + name + "&key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    private String getListsByBoardID (String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID
                + "/lists?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    private String getCardsByListID (String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/lists/" + listID +
                "/cards" + "?key=" + API_KEY + "&token=" + token, String.class).getBody();
    }

    private String getCardByID (String cardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/cards/" + cardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    private String getBoardByID (String boardID, String token) {
        return restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID + "?key=" + API_KEY
                + "&token=" + token, String.class).getBody();
    }

    private void addNewCard (String cardName, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards?idList=" + listID
                + "&name=" + cardName + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    private void addCommentToCard (String comment, String token) {
        restTemplate.postForEntity("https://api.trello.com/1/cards/" + cardID + "/actions/comments?text=" + comment
                + "&key=" + API_KEY + "&token=" + token, new HttpHeaders(), String.class);
    }

    private String formatUserDateToRFC822Date(String date) {
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

        } else if (true) {
            // next week
            // next friday
        }

        if (isDateFormatted) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(timeZone);
            return dateFormat.format(currentDate);
        }

        return null;
    }

    private void setDueDate (String RFC822date, String token, String json) {
        restTemplate.put("https://api.trello.com/1/cards/" + cardID
                + "?due=" + RFC822date + "&dueComplete=false&key=" + API_KEY + "&token=" + token, json);
    }

    private Collection<String> getMembersIDByBoardID (String boardID, String token) throws IOException {
        String json = restTemplate.getForEntity("https://api.trello.com/1/boards/" + boardID + "/memberships?key="
                + API_KEY + "&token=" + token, String.class).getBody();

        ArrayList<String> membersID = new ArrayList<>();
        for (JsonNode member : objectMapper.readTree(json)) {
            membersID.add(member.get("idMember").asText());
        }

        return membersID;
    }
}