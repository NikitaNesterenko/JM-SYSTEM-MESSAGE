package jm;

import jm.api.dao.MessageDAO;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    public List<Message> getAllMessages(Boolean isDeleted) {
        return messageDAO.getAll(isDeleted);
    }

    @Override
    public List<Message> getMessagesByChannelId(Long id, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelId(id, isDeleted);
    }

    @Override
    public List<Message> getMessagesByContent(String word, Boolean isDeleted) {
        return messageDAO.getMessageByContent(word, isDeleted);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageDAO.getById(id);
    }

    @Override
    public void createMessage(Message message) {
        messageDAO.persist(message);
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void deleteMessage(Long id) { messageDAO.deleteById(id); }

    @Override
    public void updateMessage(Message message) { messageDAO.merge(message); }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelIdForPeriod(id, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByBotIdByChannelIdForPeriod(botId, channelId, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUser(Long id, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUser(id, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUserByWorkspaceId(userId, workspaceId, isDeleted);
    }

    @Override
    public List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted) { return messageDAO.getAllMessagesReceivedFromChannelsByUserId(userId, isDeleted); }
}
