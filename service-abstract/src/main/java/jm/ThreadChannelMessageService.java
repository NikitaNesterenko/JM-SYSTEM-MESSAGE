package jm;

import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;

import java.util.List;

public interface ThreadChannelMessageService {

    void createThreadChannelMessage(ThreadChannelMessage threadChannelMessage);

    List<ThreadChannelMessage> findAll();

    List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannel(ThreadChannel threadChannel);

    List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannelId(Long id);
}
