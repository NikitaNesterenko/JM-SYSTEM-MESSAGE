package jm.api.dao;

import jm.model.message.ChannelMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface MessageDAO {

    List<ChannelMessage> getAll();

    List<ChannelMessage> getMessagesByChannelId(Long id);

    List<ChannelMessage> getMessageByContent(String word);

    List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate);

    void persist(ChannelMessage message);

    void deleteById(Long id);

    ChannelMessage merge(ChannelMessage message);

    ChannelMessage getById(Long id);

    List<ChannelMessage> getStarredMessagesForUser(Long userId);

    List<ChannelMessage> getMessagesByIds(Set<Long> ids);

}
