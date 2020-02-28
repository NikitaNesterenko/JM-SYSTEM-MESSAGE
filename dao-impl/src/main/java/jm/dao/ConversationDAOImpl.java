package jm.dao;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
import jm.model.User;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ConversationDAOImpl extends AbstractDao<Conversation> implements ConversationDAO {

    @Override
    public void persist(Conversation conversation) {
        if (
                getConversationByUsersId(conversation.getOpeningUser().getId(), conversation.getAssociatedUser().getId()).isPresent()
                        || getConversationByUsersId(conversation.getAssociatedUser().getId(), conversation.getOpeningUser().getId()).isPresent()
        ) {
            entityManager.merge(conversation);
        }
    }

    @Override
    public Optional<Conversation> getConversationByUsersId(Long firstUserId, Long secondUserId) {
        try {
            return Optional.ofNullable((Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                    .setParameter(1, firstUserId).setParameter(2, secondUserId).getSingleResult());
        } catch (NoResultException e1) {
            try {
                return Optional.ofNullable((Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                        .setParameter(1, secondUserId).setParameter(2, firstUserId).getSingleResult());
            } catch (NoResultException e2) {
                return Optional.empty();
            }
        }

    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        return (List<Conversation>) entityManager.createNativeQuery("select * from conversations where opener_id=? or associated_id=?", Conversation.class)
                .setParameter(1, userId).setParameter(2, userId).getResultList();
    }

    @Override
    public void deleteById(Long conversationID, Long userID) {
        entityManager.createNativeQuery(
                "UPDATE conversations " +
                "SET " +
                "    show_for_opener = IF(opener_id = ?, false, true), " +
                "    show_for_associated = IF(associated_id = ?, false, true) " +
                "WHERE id = ?")
                .setParameter(1, userID).setParameter(2, userID).setParameter(3, conversationID).executeUpdate();
    }
}
