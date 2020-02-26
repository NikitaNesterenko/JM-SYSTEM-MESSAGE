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
                        || getConversationByUsers(conversation.getAssociatedUser().getId(), conversation.getOpeningUser().getId()) == null
        ) {
            entityManager.merge(conversation);
        }
    }

    @Override
    public Conversation getConversationByUsers(Long firstUserId, Long secondUserId) {
        try {
            return (Conversation) entityManager.createNativeQuery("SELECT * FROM conversations WHERE (opener_id = :first_id AND associated_id = :second_id)", Conversation.class)
                    .setParameter("first_id", firstUserId).setParameter("second_id", secondUserId).getSingleResult();
        } catch (NoResultException e1) {
            try {
                return (Conversation) entityManager.createNativeQuery("SELECT * FROM conversations WHERE (opener_id = :second_id and associated_id = :first_id)", Conversation.class)
                        .setParameter("second_id", secondUserId).setParameter("first_id", firstUserId).getSingleResult();
            } catch (NoResultException e2) {
                return null;
            }
        }
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        try {
            return (List<Conversation>) entityManager.createNativeQuery("SELECT * FROM conversations WHERE opener_id = :op_id OR associated_id = :as_id", Conversation.class)
                    .setParameter("op_id", userId).setParameter("as_id", userId).getResultList();
        } catch (NoResultException e1) {
            return null;
        }
    }
}
