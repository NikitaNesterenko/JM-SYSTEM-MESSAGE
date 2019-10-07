package jm;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MessageServiceImpl implements MessageService {

    private MessageDAO messageDAO;

    @Autowired
    public MessageServiceImpl(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
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

    @Override
    public Message getMessageById(long id) {
        return messageDAO.getMessageById(id);
    }

    @Override
    public List<Message> getAllMessagesByChannel(Channel channel) {
        return messageDAO.getMessagesByChannel(channel);
    }

    @Override
    public List<Message> getAllMessagesByUser(User user) {
        return messageDAO.getMessagesByUser(user);
    }
}
