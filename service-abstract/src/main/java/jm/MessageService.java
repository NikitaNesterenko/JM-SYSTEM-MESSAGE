package jm;

import jm.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    List<Message> getAllMessages();

    List<Message> getMessagesByChannelId(Long id);

    List<Message> getMessagesByContent(String word);

    Message getMessageById(Long id);

    void createMessage(Message message);

    void deleteMessage(Long id);

    void updateMessage(Message message);

    List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate);

    List<Message> getStarredMessagesForUser(Long id);

    List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId);
}
