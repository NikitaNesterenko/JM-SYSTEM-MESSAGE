package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.model.message.DirectMessage;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class DirectMessageDAOImpl extends AbstractDao<DirectMessage> implements DirectMessageDAO {

    public List<DirectMessage> getMessagesByConversationId(Long id) {
        return entityManager.createQuery("SELECT m FROM DirectMessage m WHERE m.conversation.id =: id", DirectMessage.class)
                .setParameter("id", id)
                .getResultList();
    }
}
