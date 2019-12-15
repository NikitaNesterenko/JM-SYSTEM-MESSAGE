package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.model.message.DirectMessage;
import jm.model.message.Message;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class DirectMessageDAOImpl extends AbstractDao<DirectMessage> implements DirectMessageDAO {

    public List<DirectMessage> getMessagesByConversationId(Long id) {
        try {
            return (List<DirectMessage>) entityManager.createNativeQuery("select * from messages where conversation_id=?", Message.class)
                    .setParameter(1, id).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
