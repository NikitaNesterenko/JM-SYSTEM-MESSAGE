package jm.api.dao;

import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;

import java.util.List;

public interface ThreadChannelDAO {

    void persist(ThreadChannel threadChannel);

    ThreadChannel getById(Long id);

    ThreadChannel getByChannelMessageId(Long id);
}
