package jm;

import jm.api.dao.MessageDAO;
import jm.model.message.ChannelMessage;
import jm.model.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private MessageDAO messageDAO;

    @Autowired
    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    @Override
    public List<ChannelMessage> getAllMessages() {
        return messageDAO.getAll();
    }

    @Override
    public List<ChannelMessage> getMessagesByChannelId(Long id) {
        return messageDAO.getMessagesByChannelId(id);
    }

    @Override
    public List<ChannelMessage> getMessagesByContent(String word) {
        return messageDAO.getMessageByContent(word);
    }

    @Override
    public ChannelMessage getMessageById(Long id) {
        return messageDAO.getById(id);
    }

    @Override
    public void createMessage(ChannelMessage message) {
        messageDAO.persist(message);
    }

    @Override
    public void deleteMessage(Long id) { messageDAO.deleteById(id); }

    @Override
    public void updateMessage(ChannelMessage message) { messageDAO.merge(message); }

    @Override
    public List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return messageDAO.getMessagesByChannelIdForPeriod(id, startDate, endDate);
    }

    @Override
    public List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate) {
        return messageDAO.getMessagesByBotIdByChannelIdForPeriod(botId, channelId, startDate, endDate);
    }

    @Override
    public List<ChannelMessage> getStarredMessagesForUser(Long id) {
        return messageDAO.getStarredMessagesForUser(id);
    }
}
