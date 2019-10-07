package jm;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class MessageDAOImpl implements MessageDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Message> getAllMessages() {
        return entityManager.createQuery("from Message").getResultList();
    }

    @Override
    public void createMessage(Message message) {
        entityManager.persist(message);
    }

    @Override
    public void deleteMessage(Message message) {
        entityManager.remove(entityManager.contains(message) ? message : entityManager.merge(message));
    }

    @Override
    public void updateMessage(Message message) {
        entityManager.merge(message);
        entityManager.flush();
    }

    @Override
    public Message getMessageById(long id) {
        return entityManager.find(Message.class, id);
    }

    @Override
    public List<Message> getMessagesByUser(User user) {
        return entityManager.createQuery("from Message where owner_id = owner_id")
                .setParameter("owner_id", user.getId()).getResultList();
    }

    @Override
    public List<Message> getMessagesByChannel(Channel channel) {
        return entityManager.createQuery("from Message where owner_id = owner_id")
                .setParameter("channel_id", channel.getId()).getResultList();
    }
}
