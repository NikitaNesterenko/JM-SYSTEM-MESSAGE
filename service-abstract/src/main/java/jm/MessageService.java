package jm;

import jm.dto.MessageDTO;
import jm.model.Message;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageService {

    List<Message> getAllMessages (Boolean isDeleted);

    List<MessageDTO> getAllMessageDtoByIsDeleted (Boolean isDeleted);

    List<Message> getMessagesByChannelId (Long id, Boolean isDeleted);

    List<MessageDTO> getMessageDtoListByChannelId (Long id, Boolean isDeleted);

    List<Message> getMessagesByContent (String word, Boolean isDeleted);

    Message getMessageById (Long id);

    Optional<MessageDTO> getMessageDtoById (Long id);

    void createMessage (Message message);

    void deleteMessage (Long id);

    void updateMessage (Message message);

    List<Message> getMessagesByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<MessageDTO> getMessagesDtoByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getMessagesByBotIdByChannelIdForPeriod (Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted);

    List<Message> getStarredMessagesForUser (Long id, Boolean isDeleted);

    List<Message> getStarredMessagesForUserByWorkspaceId (Long userId, Long workspaceId, Boolean isDeleted);

    List<MessageDTO> getStarredMessagesDTOForUserByWorkspaceId (Long userId, Long workspaceId, Boolean isDeleted);

    List<Message> getAllMessagesReceivedFromChannelsByUserId (Long userId, Boolean isDeleted);

    MessageDTO getMessageDtoByMessage (@NonNull Message message);

    Message getMessageByMessageDTO (@NonNull MessageDTO messageDTO);

    Optional<Long> getMessageIdByContent (@NonNull String content);

    Optional<String> getMessageContentById (@NonNull Long id);
}
