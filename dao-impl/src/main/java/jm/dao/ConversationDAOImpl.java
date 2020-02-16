package jm.dao;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
import jm.model.User;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ConversationDAOImpl extends AbstractDao<Conversation> implements ConversationDAO {

    @Override
    public void persist(Conversation conversation) {
        if (
                getConversationByUsers(conversation.getOpeningUser().getId(), conversation.getAssociatedUser().getId()) == null
                        || getConversationByUsers(conversation.getAssociatedUser().getId(), conversation.getOpeningUser().getId()) == null
        ) {
            entityManager.merge(conversation);
        }
    }

    @Override
    public Conversation getConversationByUsers(Long firstUserId, Long secondUserId) {
        try {
            return (Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                    .setParameter(1, firstUserId).setParameter(2, secondUserId).getSingleResult();
        } catch (NoResultException e1) {
            try {
                return (Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                        .setParameter(1, secondUserId).setParameter(2, firstUserId).getSingleResult();
            } catch (NoResultException e2) {
                return null;
            }
        }
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        try {
            return (List<Conversation>) entityManager.createNativeQuery("select * from conversations where opener_id=? or associated_id=?", Conversation.class)
                    .setParameter(1, userId).setParameter(2, userId).getResultList();
        } catch (NoResultException e1) {
            return null;
        }
    }

//    @Override
//    public void deleteById(Long conversationID, Long userID) {
//        int id = entityManager.createNativeQuery("SELECT opener_id FROM conversations WHERE id=?", Conversation.class)
//                .setParameter(1, conversationID).getFirstResult();
//
//        if (id == userID) {
//            entityManager.createNativeQuery("UPDATE conversations SET show_for_opener=? WHERE opener_id=? and id=?", Conversation.class)
//                    .setParameter(1, 0).setParameter(2, userID).setParameter(3, conversationID).getResultList();
//        } else {
//            entityManager.createNativeQuery("UPDATE conversations SET show_for_associated=? WHERE associated_id=? and id=?", Conversation.class)
//                    .setParameter(1, 0).setParameter(2, userID).setParameter(3, conversationID).getResultList();
//        }p-classic_nav__team_header
//    }

    @Override
    public void deleteById(Long conversationID, Long userID) {
        entityManager.createNativeQuery("" +
                "UPDATE conversations\n" +
                "SET\n" +
                "    show_for_opener = IF(opener_id = ?, false, true),\n" +
                "    show_for_associated = IF(associated_id = ?, false, true)\n" +
                "WHERE id = ?", Conversation.class)
                .setParameter(1, userID).setParameter(2, userID).setParameter(3, conversationID).executeUpdate();
    }
}
