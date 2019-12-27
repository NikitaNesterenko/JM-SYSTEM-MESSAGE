package jm.api.dao;

import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;

import java.util.List;

public interface ThreadChannelMessageDAO {

    void persist(ThreadChannelMessage threadChannelMessage);

    List<ThreadChannelMessage> getAll();

    List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannel(ThreadChannel threadChannel);

    List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannelId(Long id);
}
