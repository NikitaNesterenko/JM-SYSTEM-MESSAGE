package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.model.message.DirectMessage;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

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
}
