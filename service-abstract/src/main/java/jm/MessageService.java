package jm;

import jm.model.message.ChannelMessage;

import java.util.List;

public interface MessageService {

    List<ChannelMessage> getAllMessages();

    List<ChannelMessage> getMessagesByChannelId(Long id);

    List<ChannelMessage> getMessagesByContent(String word);

    ChannelMessage getMessageById(Long id);

    void createMessage(ChannelMessage message);

    void deleteMessage(Long id);

    void updateMessage(ChannelMessage message);

    List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, String startDate, String endDate);

    List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, String startDate, String endDate);
}
