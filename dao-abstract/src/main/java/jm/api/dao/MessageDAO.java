package jm.api.dao;

import jm.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface MessageDAO {

    List<Message> getAll();

    List<Message> getMessagesByChannelId(Long id);

    List<Message> getMessageByContent(String word);

    List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate);

    List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId);

    void persist(Message message);

    void deleteById(Long id);

    Message merge(Message message);

    Message getById(Long id);

    List<Message> getStarredMessagesForUser(Long userId);

    List<Message> getMessagesByIds(Set<Long> ids);

}
