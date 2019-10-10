package jm.dao;

import jm.api.dao.MessageDAO;
import jm.model.Message;
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
        return entityManager.createNativeQuery("SELECT * FROM messages", Message.class).getResultList();
    }

    @Override
    public Message getMessageById(Long id) {
        return entityManager.find(Message.class, id);
    }

    @Override
    public void createMessage(Message message) {
        entityManager.persist(message);
    }

    @Override
    public void deleteMessage(Message message) {
        Message searchedMessage = entityManager.find(Message.class, message.getId());
        if (searchedMessage != null) {
            entityManager.remove(searchedMessage);
        }
    }

    @Override
    public void updateMessage(Message message) {
        entityManager.merge(message);
        entityManager.flush();
    }
}
