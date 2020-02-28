package jm.api.dao;

import jm.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface MessageDAO {

    List<Message> getAll(Boolean isDeleted);

    List<Message> getMessagesByChannelId(Long id, Boolean isDeleted);

    List<Message> getMessageByContent(String word, Boolean isDeleted);

    List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted);

    void persist(Message message);

    void deleteById(Long id);

    Message merge(Message message);

    Message getById(Long id);

    List<Message> getStarredMessagesForUser(Long userId, Boolean isDeleted);

    List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted);

    List<Message> getMessagesByIds(Set<Long> ids, Boolean isDeleted);

}
