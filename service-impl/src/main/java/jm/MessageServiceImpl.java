package jm;

import jm.api.dao.MessageDAO;
import jm.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private MessageDAO messageDAO;

    @Autowired
    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    @Override
    public List getAllMessages() {
        return messageDAO.getAllMessages();
    }

    @Override
    public Message getMessageById(Long id) {
        return messageDAO.getMessageById(id);
    }

    @Override
    public void createMessage(Message message) {
        messageDAO.createMessage(message);
    }

    @Override
    public void deleteMessage(Message message) {
        messageDAO.deleteMessage(message);
    }

    @Override
    public void updateMessage(Message message) {
        messageDAO.updateMessage(message);
    }
}
