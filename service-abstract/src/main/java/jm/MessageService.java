package jm;

import jm.model.message.ChannelMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    List<ChannelMessage> getAllMessages();

    List<ChannelMessage> getMessagesByChannelId(Long id);

    List<ChannelMessage> getMessagesByContent(String word);

    ChannelMessage getMessageById(Long id);

    void createMessage(ChannelMessage message);

    void deleteMessage(Long id);

    void updateMessage(ChannelMessage message);

    List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate);

    List<ChannelMessage> getStarredMessagesForUser(Long id);
}
