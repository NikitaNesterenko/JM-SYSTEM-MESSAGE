package jm.api.dao;

import jm.dto.MessageDTO;
import jm.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MessageDAO {

    List<Message> getAll(Boolean isDeleted);

    List<MessageDTO> getAllMessageDtoByIsDeleted(Boolean isDeleted);

    List<Message> getMessagesByChannelId(Long id, Boolean isDeleted);

    List<MessageDTO> getMessageDtoListByChannelId(Long id, Boolean isDeleted);

    List<Message> getMessageByContent(String word, Boolean isDeleted);

    List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<MessageDTO> getMessagesDtoByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted);

    void persist(Message message);

    void deleteById(Long id);

    Message merge(Message message);

    Message getById(Long id);

    Optional<MessageDTO> getMessageDtoById(Long id);

    List<Message> getStarredMessagesForUser(Long userId, Boolean isDeleted);

    List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted);

    List<MessageDTO> getStarredMessagesDTOForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted);

    List<Message> getMessagesByIds(Set<Long> ids, Boolean isDeleted);

    Optional<Long> getMessageIdByContent(String content);

    Optional<String> getMessageContentById(Long id);

    LocalDateTime getDateCreateById(Long id);

    byte[] getVoiceMessageSoundById(long id);

    String getVoiceMessagePathById(long id);

    List<Message> getAllMessagesAssociatedWithUser(Long userId, Boolean isDeleted);

}
