package jm;

import jm.model.Conversation;
import jm.model.User;

import java.util.List;

public interface ConversationService {

    List<Conversation> gelAllConversations();

    void createConversation(Conversation conversation);

    void deleteConversation(Long id);

    Conversation updateConversation(Conversation conversation);

    Conversation getConversationById(Long id);

    Conversation getConversationByUsers(Long firstUserId, Long secondUserId);

    List<Conversation> getConversationsByUserId(Long userId);

}
