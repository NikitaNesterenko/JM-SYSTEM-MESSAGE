package jm;

import jm.dto.ConversationDTO;
import jm.model.Conversation;
import java.util.List;

public interface ConversationService {

    List<Conversation> getAllConversations();

    void createConversation(Conversation conversation);

    void deleteConversation(Long conversationID, Long userID);

    Conversation updateConversation(Conversation conversation);

    Conversation getConversationById(Long id);

    Conversation getConversationByUsersId(Long firstUserId, Long secondUserId);

    List<Conversation> getConversationsByUserId(Long userId);

    Conversation getEntityFromDTO(ConversationDTO conversationDTO);
}
