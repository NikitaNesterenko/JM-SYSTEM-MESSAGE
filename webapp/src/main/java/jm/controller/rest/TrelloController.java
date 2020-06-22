package jm.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.TrelloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/rest/api/trello")
@Tag(name = "trello", description = "Trello API")
public class TrelloController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TrelloService trelloService;
    private String boardID;
    private String listID;
    private String cardID;

    public TrelloController(TrelloService trelloService) {
        this.trelloService = trelloService;
    }

    @Operation(
            description = "Метод устанавливает токен Trello для текущего авторизованного пользователя.",
            operationId = "setUserTrelloToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trello token was set!"
                    )
            })
    @PostMapping(value = "/token")
    public ResponseEntity<String> setUserToken(@RequestParam(name = "value") String token) {
        trelloService.setToken(token);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Метод позволяет достать токен из бд",
            operationId = "hasTrelloToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trello token is not null!"),
                    @ApiResponse(responseCode = "400", description = "Trello token in null!")
            })
    @GetMapping(value = "/hasTrelloToken")
    public ResponseEntity<String> hasTrelloToken() {
        String token = trelloService.getTokenByUserLogin();
        return token == null ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(
            description = "Метод позволяет получить информацию (JSON объект) о текущей доске.",
            operationId = "getBoardInfo",
            summary = "get Board Info",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Token is null")
            })
    @GetMapping(value = "info")
    public ResponseEntity<String> getBoardInfo(@RequestParam(name = "value") String boardID) {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        String boardJSON = trelloService.getBoardByBoardID(boardID, token);
        return ResponseEntity.ok(boardJSON);
    }

    @Operation(
            description = "Метод позволяет получить объект доски или карточки по ссылке на них. Также устанавливает" +
                    "текущую доску, если в качестве параметра передано URL доски и доску/список/карточку, если в качестве" +
                    "параметра передано URL карточки.",
            operationId = "getBoardOrCardDetails",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Token is null")
            })
    @GetMapping(value = "/details")
    public ResponseEntity<String> getBoardOrCardDetails(@RequestParam(name = "url") String URL) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isBoardURL = false;
        boolean isCardURL = false;
        if (URL.startsWith("https://trello.com/b/")) {
            isBoardURL = true;
            URL.replace("https://trello.com/b/", "https://api.trello.com/1/boards/");
        }
        if (URL.startsWith("https://trello.com/c/")) {
            isCardURL = true;
            URL.replace("https://trello.com/c/", "https://api.trello.com/1/cards/");
        }

        if (isBoardURL || isCardURL) {
            String boardOrCardJSON = trelloService.getBoardOrCardByURL(URL, token);
            if (boardOrCardJSON == null) {
                return ResponseEntity.badRequest().build();
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
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @Operation(
            description = "Метод позволяет получить объект доски по ссылке на неё. При поиске по имени возвращает все" +
                    "подходящие доски. Устанавливает текущую доску, если была передана ссылка.",
            operationId = "linkBoard",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Token is null")
            })
    @PostMapping(value = "/link")
    public ResponseEntity<String> linkBoard(
            @RequestParam(name = "pointer") String pointer) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
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

                return ResponseEntity.ok(pointerResponseJSON);
            } else {

                JsonNode boardsNode = objectMapper.readTree(pointerResponseJSON).get("boards");

                if (boardsNode.size() == 0) {
                    return ResponseEntity.badRequest().build();
                }

                StringBuilder boards = new StringBuilder();
                for (JsonNode boardNode : boardsNode) {
                    String boardID = boardNode.get("id").asText();
                    String boardJSON = trelloService.getBoardByBoardID(boardID, token);
                    boards.append(boardJSON);
                    boards.append(",");
                }
                boards.deleteCharAt(boards.length() - 1);

                return ResponseEntity.ok(boards.toString());
            }
        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            description = "Если передан параметр позиции списка на доске, метод устанавливает текущий список. Если метод" +
                    "вызван без параметров, то возвращает все списки.",
            operationId = "setList",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Token is null")
            })
    @PostMapping(value = "/list")
    public ResponseEntity<String> setList(@RequestParam(name = "pos") String listPosition) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        if (boardID == null) {
            return ResponseEntity.badRequest().build();
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
                        return ResponseEntity.ok().build();
                    }
                    currentListPos++;
                }
            }
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(listsJSON);
    }

    @Operation(
            description = "Метод создаёт новую карточку, делая её текущей.",
            operationId = "addCard",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Token is null")
            })
    @PostMapping(value = "/addCard")
    public ResponseEntity<String> addCard(
            @RequestParam(name = "name") String cardName) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        if (listID == null) {
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Метод устанавливает срок истечения для установленной карточки.",
            operationId = "setDueDate",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @PostMapping(value = "/due")
    public ResponseEntity<String> setDueDate(
            @RequestParam(name = "week") String week,
            @RequestParam(name = "day") int day,
            @RequestParam(name = "hour") int hour) {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        if (cardID == null) {
            return ResponseEntity.badRequest().body("CardId id null");
        }

        String RFC822Date = trelloService.formatStringToRFC822Date(week, day, hour);
        if (RFC822Date == null) {
            return ResponseEntity.badRequest().build();
        }

        String cardJSON = trelloService.getCardByCardID(cardID, token);
        trelloService.setCardDueDate(RFC822Date, cardID, cardJSON, token);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Метод добавляет комментарий к установленной карточке.",
            operationId = "addComment",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @PostMapping(value = "/comment")
    public ResponseEntity<String> addComment(
            @RequestParam(name = "text") String comment) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        if (cardID == null) {
            return ResponseEntity.badRequest().build();
        }

        trelloService.addCommentToCard(comment, cardID, token);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Метод возвращает набор карточек, подходящих по запросу.",
            operationId = "performSearch",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @GetMapping(value = "/search")
    public ResponseEntity<String> performSearch(
            @RequestParam(name = "name") String name) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        String nameResponseJSON = trelloService.getBoardOrCardByName(name, token);
        try {
            JsonNode cardsNode = objectMapper.readTree(nameResponseJSON).get("cards");
            if (cardsNode.size() == 0) {
                return ResponseEntity.badRequest().build();
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
                return ResponseEntity.ok(cards.toString());
            }
        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @Operation(
            description = "Метод добавляет пользователя к участникам установленной карточки.",
            operationId = "assignMember",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @PostMapping(value = "assign")
    public ResponseEntity<String> assignMember(
            @RequestParam(name = "name") String userName) {

        String userToken = trelloService.getTokenByUserName(userName);
        if (userToken == null) {
            return ResponseEntity.badRequest().build();
        }

        String userID = trelloService.getUserIDByUserToken(userToken);
        trelloService.assignUserToCardByUserID(userID, cardID, userToken);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Удаление доски",
            operationId = "delete Board",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @DeleteMapping(value = "deleteBoard")
    public ResponseEntity<String> deleteBoard(
            @RequestParam(name = "id") String boardID,
            @RequestParam(name = "name") String userName) {

        String userToken = trelloService.getTokenByUserName(userName);
        if (userToken == null) {
            return ResponseEntity.badRequest().build();
        }

        trelloService.deleteBoard(boardID, userToken);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Удаление карточки",
            operationId = "delete card",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @DeleteMapping(value = "deleteCard")
    public ResponseEntity<String> deleteCard(
            @RequestParam(name = "id") String cardID) {

        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        trelloService.deleteCard(boardID, token);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Создание новой доски",
            operationId = "add Board",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "don't success")
            })
    @PostMapping(value = "addBoard")
    public ResponseEntity<String> addBoard(
            @RequestParam(name = "name") String boardName) {
        String token = trelloService.getTokenByUserLogin();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }

        trelloService.addBoard(boardName, token);
        return ResponseEntity.ok().build();
    }

}
