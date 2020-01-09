package jm;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private ConversationDAO conversationDAO;

    @Autowired
    public void setConversationDAO(ConversationDAO conversationDAO) {
        this.conversationDAO = conversationDAO;
    }

    @Override
    public List<Conversation> gelAllConversations() {
        return conversationDAO.getAll();
    }

    @Override
    public void createConversation(Conversation conversation) {
        conversationDAO.persist(conversation);
    }

    @Override
    public void deleteConversation(Long id) {
        conversationDAO.deleteById(id);
    }

    @Override
    public Conversation updateConversation(Conversation conversation) {
        return conversationDAO.merge(conversation);
    }

    @Override
    public Conversation getConversationById(Long id) {
        return conversationDAO.getById(id);
    }

    @Override
    public Conversation getConversationByUsers(Long firstUserId, Long secondUserId) {
        return conversationDAO.getConversationByUsers(firstUserId, secondUserId);
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        return conversationDAO.getConversationsByUserId(userId);
    }
}
