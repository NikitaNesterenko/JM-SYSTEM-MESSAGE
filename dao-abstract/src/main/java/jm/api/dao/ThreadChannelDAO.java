package jm.api.dao;

import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;

import java.util.List;
import java.util.Optional;

public interface ThreadChannelDAO {

    void persist(ThreadChannel threadChannel);

    ThreadChannel getById(Long id);

    Optional<ThreadChannel> getByChannelMessageId(Long id);
}
