package jm.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.TrelloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**
 * Класс контроллер Trello.
 * https://docs.google.com/document/d/1KQVn9yOx-I3dqy3l1E-3TfiTcGA-GLVR-A0MRvXbTCE/edit?usp=sharing
 */

@RestController
@RequestMapping("/api/trello")
public class TrelloController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TrelloServiceImpl trelloService;
    private String boardID;
    private String listID;
    private String cardID;

    @Autowired
    public void setTrelloService (TrelloServiceImpl trelloService) {
        this.trelloService = trelloService;
    }

    /**
     * Метод устанавливает токен Trello для текущего авторизованного пользователя.
     *
     * @param token - токен Trello.
     */

    @PostMapping (value = "/token")
    public ResponseEntity <String> setUserToken (@RequestParam (name = "value") String token) {
        trelloService.setToken(token);
        return new ResponseEntity<> ("Trello token was set!", HttpStatus.OK);
    }

    /**
     * Метод позволяет получить информацию (JSON объект) о текущей доске.
     *
     */

    @GetMapping (value = "info")
    public ResponseEntity<String> getBoardInfo () {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }
        String boardJSON = trelloService.getBoardByBoardID(boardID, token);
        return new ResponseEntity<>(boardJSON, HttpStatus.OK);
    }

    /**
     * Метод позволяет получить объект доски или карточки по ссылке на них. Также устанавливает текущую доску, если в
     * качестве параметра передано URL доски и доску/список/карточку, если в качестве параметра передано URL карточки.
     *
     * @param URL - ссылка на доску или карточку.
     */

    /*
    Slack не позволяет создавать карточки после установки текущей карточки через её URL и просит установить доску командой
    /trello link {board URL}. Отказался от этого решения, добавив установку доски/списка/карточки.
     */

    @GetMapping (value = "/details")
    public ResponseEntity<String> getBoardOrCardDetails (@RequestParam (name = "url") String URL) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        boolean isBoardURL = false;
        boolean isCardURL = false;
        if (URL.startsWith("https://trello.com/b/")) {
            isBoardURL = true;
        }
        if (URL.startsWith("https://trello.com/c/")) {
            isCardURL = true;
        }

        if (isBoardURL || isCardURL) {
            String json = trelloService.getBoardOrCardByURL(URL, token);
            if (json == null) {
                return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
            }

            try {
                JsonNode jsonNode = objectMapper.readTree(json);
                if (isBoardURL) {
                    boardID = jsonNode.get("id").asText();
                }
                if (isCardURL) {
                    boardID = jsonNode.get("idBoard").asText();
                    listID = jsonNode.get("idList").asText();
                    cardID = jsonNode.get("id").asText();
                }
            } catch (IOException ex) {
                return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            return new ResponseEntity<>(json, HttpStatus.OK);
        }

        return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
    }

    /**
     * Метод позволяет получить объект доски по ссылке на неё. При поиске по имени возвращает все подходящие доски.
     * Устанавливает текущую доску, если была передана ссылка.
     *
     * @param pointer - указатель на доску. Может быть как ссылкой, так и именем.
     */

    @PostMapping (value = "/link")
    public ResponseEntity<String> linkBoard (
            @RequestParam (name = "pointer") String pointer) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        boolean isBoardURL = false;
        if (pointer.startsWith("https://trello.com/b/")) {
            isBoardURL = true;
        }

        String json = trelloService.getBoardOrCardByName(pointer, token);
        try {
            if (isBoardURL) {

                JsonNode boardNode = objectMapper.readTree(json);
                boardID = boardNode.get("id").asText();

                return new ResponseEntity<>(json, HttpStatus.OK);
            } else {

                JsonNode boardsNode = objectMapper.readTree(json).get("boards");

                if (boardsNode.size() == 0) {
                    return new ResponseEntity<>("Cant find any boards!", HttpStatus.BAD_REQUEST);
                }

                StringBuilder boards = new StringBuilder();
                for (JsonNode boardNode : boardsNode) {
                    String boardID = boardNode.get("id").asText();
                    String boardJSON = trelloService.getBoardByBoardID(boardID, token);
                    boards.append(boardJSON);
                    boards.append(",");
                }
                boards.deleteCharAt(boards.length() - 1);

                return new ResponseEntity<>(boards.toString(), HttpStatus.OK);
            }
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * Если передан параметр позиции списка на доске, метод устанавливает текущий список. Если метод вызван без параметров,
     * то возвращает все списки.
     *
     * @param listPosition - позиция
     */

    @PostMapping (value = "/list")
    public ResponseEntity<String> setList (@RequestParam (name = "pos") String listPosition) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (boardID == null) {
            return new ResponseEntity<>("Board not set!", HttpStatus.BAD_REQUEST);
        }

        String json = trelloService.getListsByBoardID(boardID, token);
        try {
            JsonNode listsNode = objectMapper.readTree(json);
            if (listPosition != null) {
                int listPos = Integer.parseInt(listPosition);
                int currentListPos = 1;
                for (JsonNode listNode : listsNode) {
                    if (currentListPos == listPos) {
                        listID = listNode.get("id").asText();
                        return new ResponseEntity<>("List set!", HttpStatus.OK);
                    }
                    currentListPos++;
                }
            }
        } catch (NumberFormatException ex) {
            return new ResponseEntity<>("Error in list position!", HttpStatus.BAD_REQUEST);
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    /**
     * Метод создаёт новую карточку, делая её текущей.
     *
     * @param cardName - имя создаваемой карточки.
     */

    @PostMapping (value = "/add")
    public ResponseEntity<String> addCard (
            @RequestParam (name = "name") String cardName) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (listID == null) {
            return new ResponseEntity<>("List not set!", HttpStatus.BAD_REQUEST);
        }

        trelloService.addNewCard(cardName, listID, token);
        String json = trelloService.getCardsByListID(listID, token);
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            for (JsonNode cardNode : jsonNode) {
                String currentNodeCardName = cardNode.get("name").asText();
                if (cardName.equals(currentNodeCardName)) {
                    cardID = cardNode.get("id").asText();
                }
            }
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>("New card was created successfully!", HttpStatus.OK);
    }

    /**
     * Метод устанавливает срок истечения для установленной карточки.
     *
     * @param date - дата истечения карточки.
     */

    @PostMapping (value = "/due")
    public ResponseEntity<String> setDueDate (@RequestParam (name = "date") String date) {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (cardID == null) {
            return new ResponseEntity<>("Card not set!", HttpStatus.BAD_REQUEST);
        }

        String RFC822Date = trelloService.formatStringToRFC822Date(date);
        if (RFC822Date == null) {
            return new ResponseEntity<>("Error in date!", HttpStatus.BAD_REQUEST);
        }

        String json = trelloService.getCardByCardID(cardID, token);
        trelloService.setCardDueDate(RFC822Date, cardID, token, json);
        return new ResponseEntity<>("Due set!", HttpStatus.OK);
    }

    /**
     * Метод добавляет комментарий к установленной карточке.
     *
     * @param comment - текст комментария.
     */

    @PostMapping (value = "/comment")
    public ResponseEntity<String> addComment (
            @RequestParam (name = "text") String comment) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (cardID == null) {
            return new ResponseEntity<>("Card not set!", HttpStatus.BAD_REQUEST);
        }

        trelloService.addCommentToCard(comment, cardID, token);
        return new ResponseEntity<>("Comment was added successfully!", HttpStatus.OK);
    }

    /**
     * Метод возвращает набор карточек, подходящих по запросу.
     *
     * @param name - имя искомой карточки.
     */

    @GetMapping (value = "/search")
    public ResponseEntity<String> performSearch (
            @RequestParam (name = "name") String name) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        String json = trelloService.getBoardOrCardByName(name, token);
        try {
            JsonNode cardsNode = objectMapper.readTree(json).get("cards");
            if (cardsNode.size() == 0) {
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
        } catch (IOException ex) {
            return new ResponseEntity<>("Unexpected error!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>("Cant find any cards!", HttpStatus.BAD_REQUEST);
    }

    /**
     * Метод добавляет пользователя к участникам установленной карточки.
     *
     * @param userName - имя добавляемого пользователя.
     */

    @PostMapping (value = "assign")
    public ResponseEntity<String> assignMember (
            @RequestParam (name = "name") String userName) {

        String userToken = trelloService.getTokenByUserName(userName);
        if (userToken == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        String userID = trelloService.getUserIDByUserToken(userToken);
        trelloService.assignUserToCardByUserID(userID, cardID, userToken);
        return new ResponseEntity<>("User assigned successfully!", HttpStatus.OK);
    }

    /**
     * Метод получает отзывы пользователей.
     *
     * @param feedback - текст отзыва.
     */

    @PostMapping (value = "feedback")
    public ResponseEntity<String> getFeedback (
            @RequestParam (name = "text") String feedback) {

        // Какие-то действия с отзывом пользователя...

        return new ResponseEntity<>("Feedback received!", HttpStatus.OK);
    }
}
