package jm.api.dao;

import jm.model.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationDAO {

    List<Conversation> getAll();

    void persist(Conversation conversation);

    void deleteById(Long conversationID, Long userID);

    Conversation merge(Conversation conversation);

    Conversation getById(Long id);

    Optional <Conversation> getConversationByUsersId(Long firstUserId, Long secondUserId);

    List<Conversation> getConversationsByUserId(Long userId);
}
