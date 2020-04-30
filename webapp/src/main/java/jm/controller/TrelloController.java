package jm.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import jm.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/trello")
public class TrelloController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TrelloService trelloService;
    private String boardID;
    private String listID;
    private String cardID;

    @Autowired
    public void setTrelloService (TrelloService trelloService) {
        this.trelloService = trelloService;
    }

    @Operation (
            description = "Метод устанавливает токен Trello для текущего авторизованного пользователя."
    )
    @PostMapping (value = "/token")
    public ResponseEntity <String> setUserToken (@RequestParam (name = "value") String token) {
        trelloService.setToken(token);
        return new ResponseEntity<> ("Trello token was set!", HttpStatus.OK);
    }

    @Operation (
            description = "Метод позволяет получить информацию (JSON объект) о текущей доске."
    )
    @GetMapping (value = "info")
    public ResponseEntity<String> getBoardInfo () {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }
        String boardJSON = trelloService.getBoardByBoardID(boardID, token);
        return new ResponseEntity<>(boardJSON, HttpStatus.OK);
    }

    @Operation (
            description = "Метод позволяет получить объект доски или карточки по ссылке на них. Также устанавливает" +
                    "текущую доску, если в качестве параметра передано URL доски и доску/список/карточку, если в качестве" +
                    "параметра передано URL карточки."
    )
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
            String boardOrCardJSON = trelloService.getBoardOrCardByURL(URL, token);
            if (boardOrCardJSON == null) {
                return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
            }

            try {
                JsonNode jsonNode = objectMapper.readTree(boardOrCardJSON);
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

            return new ResponseEntity<>(boardOrCardJSON, HttpStatus.OK);
        }

        return new ResponseEntity<>("Error in URL!", HttpStatus.BAD_REQUEST);
    }

    @Operation (
            description = "Метод позволяет получить объект доски по ссылке на неё. При поиске по имени возвращает все" +
                    "подходящие доски. Устанавливает текущую доску, если была передана ссылка."
    )
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

        String pointerResponseJSON = trelloService.getBoardOrCardByName(pointer, token);
        try {
            if (isBoardURL) {

                JsonNode boardNode = objectMapper.readTree(pointerResponseJSON);
                boardID = boardNode.get("id").asText();

                return new ResponseEntity<>(pointerResponseJSON, HttpStatus.OK);
            } else {

                JsonNode boardsNode = objectMapper.readTree(pointerResponseJSON).get("boards");

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

    @Operation (
            description = "Если передан параметр позиции списка на доске, метод устанавливает текущий список. Если метод" +
                    "вызван без параметров, то возвращает все списки."
    )
    @PostMapping (value = "/list")
    public ResponseEntity<String> setList (@RequestParam (name = "pos") String listPosition) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (boardID == null) {
            return new ResponseEntity<>("Board not set!", HttpStatus.BAD_REQUEST);
        }

        String listsJSON = trelloService.getListsByBoardID(boardID, token);
        try {
            JsonNode listsNode = objectMapper.readTree(listsJSON);
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

        return new ResponseEntity<>(listsJSON, HttpStatus.OK);
    }

    @Operation (
            description = "Метод создаёт новую карточку, делая её текущей."
    )
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
        String cardJSON = trelloService.getCardsByListID(listID, token);
        try {
            JsonNode jsonNode = objectMapper.readTree(cardJSON);
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

    @Operation (
            description = "Метод устанавливает срок истечения для установленной карточки."
    )
    @PostMapping (value = "/due")
    public ResponseEntity<String> setDueDate (
            @RequestParam (name = "week") String week,
            @RequestParam (name = "day") int day,
            @RequestParam (name = "hour") int hour) {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        if (cardID == null) {
            return new ResponseEntity<>("Card not set!", HttpStatus.BAD_REQUEST);
        }

        String RFC822Date = trelloService.formatStringToRFC822Date(week, day, hour);
        if (RFC822Date == null) {
            return new ResponseEntity<>("Error in date!", HttpStatus.BAD_REQUEST);
        }

        String cardJSON = trelloService.getCardByCardID(cardID, token);
        trelloService.setCardDueDate(RFC822Date, cardID, cardJSON, token);
        return new ResponseEntity<>("Due set!", HttpStatus.OK);
    }

    @Operation (
            description = "Метод добавляет комментарий к установленной карточке."
    )
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

    @Operation (
            description = "Метод возвращает набор карточек, подходящих по запросу."
    )
    @GetMapping (value = "/search")
    public ResponseEntity<String> performSearch (
            @RequestParam (name = "name") String name) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return new ResponseEntity<>("Token is empty!", HttpStatus.UNAUTHORIZED);
        }

        String nameResponseJSON = trelloService.getBoardOrCardByName(name, token);
        try {
            JsonNode cardsNode = objectMapper.readTree(nameResponseJSON).get("cards");
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

    @Operation (
            description = "Метод добавляет пользователя к участникам установленной карточки."
    )
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

    @Operation (
            description = "Метод получает отзывы пользователей."
    )
    @PostMapping (value = "feedback")
    public ResponseEntity<String> getFeedback (
            @RequestParam (name = "text") String feedback) {

        // Какие-то действия с отзывом пользователя...

        return new ResponseEntity<>("Feedback received! Feedback text: " + feedback, HttpStatus.OK);
    }
}
