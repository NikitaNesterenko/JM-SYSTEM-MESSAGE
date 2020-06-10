package jm;

public interface TrelloService {
    String getTokenByUserLogin();

    String getTokenByUserName(String userName);

    String getBoardOrCardByURL(String URL, String token);

    String getBoardOrCardByName(String pointer, String token);

    String getListsByBoardID(String boardID, String token);

    String getCardsByListID(String listID, String token);

    String getCardByCardID(String cardID, String token);

    String getBoardByBoardID(String boardID, String token);

    String getUserIDByUserToken(String token);

    String getAction(String id, String token);

    String updateBoardName(String id, String name, String token);

    String createList(String id, String name, String token);

    String updateListName(String id, String name, String token);

    String deleteBoard(String boardID, String userToken);

    String deleteCard(String boardID, String token);

    String addBoard(String boardName, String token);

    void setToken(String token);

    void setCardDueDate(String RFC822date, String cardID, String cardJSON, String token);

    String addNewCard(String cardName, String listID, String token);

    String addCommentToCard(String comment, String cardID, String token);

    void assignUserToCardByUserID(String userID, String cardID, String token);

    String formatStringToRFC822Date(String week, int day, int hour);
}
