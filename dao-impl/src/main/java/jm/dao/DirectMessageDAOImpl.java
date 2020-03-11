package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.dto.DirectMessageDTO;
import jm.model.message.DirectMessage;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class DirectMessageDAOImpl extends AbstractDao<DirectMessage> implements DirectMessageDAO {

    public List<DirectMessage> getMessagesByConversationId(Long id, Boolean isDeleted) {
        return entityManager.createQuery("select m from DirectMessage m " +
                "where m.conversation.id =: id and m.isDeleted =: isDeleted", DirectMessage.class)
                .setParameter("id", id)
                .setParameter("isDeleted", isDeleted)
                .getResultList();
    }

    @Override
    public Optional<Long> getConversationIdByMessageId(Long messageId) {
        Long conversationId = null;
        try {
            conversationId = (Long) entityManager.createNativeQuery("SELECT dm.conversation_id FROM direct_messages dm WHERE dm.id=:messageId ")
                    .setParameter("messageId", messageId)
                    .getSingleResult();

        } catch (NoResultException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(conversationId);
    }
}
