package jm.dao;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
import jm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ConversationDAOImpl extends AbstractDao<Conversation> implements ConversationDAO {

    @Override
    public void persist(Conversation conversation) {
        if (getConversationByUsers(conversation.getOpeningUser().getId(), conversation.getAssociatedUser().getId()) == null
                        || getConversationByUsers(conversation.getAssociatedUser().getId(), conversation.getOpeningUser().getId()) == null) {
            entityManager.merge(conversation);
        }
    }

    @Override
    public Conversation getConversationByUsers(Long firstUserId, Long secondUserId) {
        try {
            return (Conversation) entityManager.createNativeQuery("SELECT * FROM conversations WHERE (opener_id=? AND associated_id=?)", Conversation.class)
                    .setParameter(1, firstUserId)
                    .setParameter(2, secondUserId)
                    .getSingleResult();
        } catch (NoResultException e1) {
            try {
                return (Conversation) entityManager.createNativeQuery("SELECT * FROM conversations WHERE (opener_id=? AND associated_id=?)", Conversation.class)
                        .setParameter(1, secondUserId)
                        .setParameter(2, firstUserId)
                        .getSingleResult();
            } catch (NoResultException e2) { return null; }
        }
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
            return (List<Conversation>) entityManager.createNativeQuery("select * from conversations where opener_id=? or associated_id=?", Conversation.class)
                    .setParameter(1, userId)
                    .setParameter(2, userId)
                    .getResultList();
    }
}
