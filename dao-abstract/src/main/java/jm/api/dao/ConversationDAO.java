package jm.api.dao;

import jm.model.Conversation;
import jm.model.User;

import java.util.List;

public interface ConversationDAO {

    List<Conversation> getAll();

    void persist(Conversation conversation);

    void deleteById(Long conversationID, Long userID);

    Conversation merge(Conversation conversation);

    Conversation getById(Long id);

    Conversation getConversationByUsersId(Long firstUserId, Long secondUserId);

    List<Conversation> getConversationsByUserId(Long userId);

    void showConversation(Long ConversationID, Long userID);
}
