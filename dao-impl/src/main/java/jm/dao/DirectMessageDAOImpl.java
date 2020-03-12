package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.model.Message;
import jm.model.message.DirectMessage;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    public List<DirectMessage> getMessagesByIds(Set<Long> ids, Boolean isDeleted) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select m from DirectMessage m where m.id in :ids and m.isDeleted = :is_deleted", DirectMessage.class)
                .setParameter("ids", ids)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }
}
