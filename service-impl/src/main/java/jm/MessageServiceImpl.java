package jm;

import jm.api.dao.MessageDAO;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private MessageDAO messageDAO;

    @Autowired
    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }


    @Override
    public List<Message> getAllMessages() {
        return messageDAO.getAll();
    }

    @Override
    public List<Message> getMessagesByContent(String word) {
        return messageDAO.getMessageByContetn(word);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageDAO.getById(id);
    }

    @Override
    public void createMessage(Message message) {
        messageDAO.persist(message);
    }

    @Override
    public void deleteMessage(Long id) {
        messageDAO.deleteById(id);

    }

    @Override
    public void updateMessage(Message message) {
        messageDAO.merge(message);

    }
}
