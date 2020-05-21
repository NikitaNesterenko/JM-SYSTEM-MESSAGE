package jm;

import jm.api.dao.ConversationDAO;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.dto.ConversationDTO;
import jm.model.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private ConversationDAO conversationDAO;
    private UserDAO userDAO;
    private WorkspaceDAO workspaceDAO;

    @Autowired
    public void setConversationDAO(ConversationDAO conversationDAO, UserDAO userDAO, WorkspaceDAO workspaceDAO) {
        this.conversationDAO = conversationDAO;
        this.userDAO = userDAO;
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public List<Conversation> getAllConversations() {
        try {
            return conversationDAO.getAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void createConversation(Conversation conversation) {
        Long firstUserId = conversation.getOpeningUser().getId();
        Long secondUserId = conversation.getAssociatedUser().getId();
        Conversation conversationFromBase = getConversationByUsersId(firstUserId, secondUserId);
        Long setSecondId = getConversationByUsersId(secondUserId, firstUserId).getId();
        if (conversationFromBase.getId() == null || setSecondId == null) {
            conversationDAO.persist(conversation);
        } else {
            conversation.setId(conversationFromBase.getId());
            updateConversation(conversation);
        }
    }


    @Async("threadPoolTaskExecutor")
    @Override
    public void deleteConversation(Long conversationID, Long userID) {
        conversationDAO.deleteById(conversationID, userID);
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
    public Conversation getConversationByUsersId(Long firstUserId, Long secondUserId) {
            return conversationDAO.getConversationByUsersId(firstUserId, secondUserId).orElse(new Conversation());
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        return conversationDAO.getConversationsByUserId(userId);
    }

    @Override
    public Conversation getEntityFromDTO(ConversationDTO conversationDTO) {
        if (conversationDTO == null) {
            return null;
        }
        Conversation conversation = new Conversation();
        conversation.setOpeningUser(userDAO.getById(conversationDTO.getOpeningUserId()));
        conversation.setAssociatedUser(userDAO.getById(conversationDTO.getAssociatedUserId()));
        conversation.setWorkspace(workspaceDAO.getById(conversationDTO.getWorkspaceId()));
        conversation.setShowForAssociated(conversationDTO.getShowForAssociated());
        conversation.setShowForOpener(conversationDTO.getShowForOpener());

        return conversation;
    }


}
