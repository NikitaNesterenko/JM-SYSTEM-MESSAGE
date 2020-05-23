package jm.dao;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
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
        entityManager.persist(conversation);
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
            } catch (NoResultException ignored) {
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        try {
            return (List<Conversation>) entityManager.createNativeQuery(
                    "select * from conversations " +
                            "where opener_id=? and show_for_opener=true " +
                            "   or associated_id=? and show_for_associated=true", Conversation.class)
                    .setParameter(1, userId).setParameter(2, userId).getResultList();
        } catch (NoResultException e1) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteById(Long conversationID, Long userID) {
        entityManager.createNativeQuery(
                "UPDATE conversations " +
                        "SET " +
                        "    show_for_opener = IF(opener_id = ? ^ show_for_opener = false , false, true), " +
                        "    show_for_associated = IF(associated_id = ? ^ show_for_associated = false, false, true) " +
                        "WHERE id = ?")
                .setParameter(1, userID).setParameter(2, userID).setParameter(3, conversationID).executeUpdate();
    }
}
