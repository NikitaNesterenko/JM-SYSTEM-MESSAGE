package jm.api.dao;

import jm.model.ThreadChannel;
import lombok.NonNull;

import java.util.Optional;

public interface ThreadChannelDAO {

    void persist(ThreadChannel threadChannel);

    ThreadChannel getById(Long id);

    ThreadChannel getByChannelMessageId(Long id);

    Optional<Number> getThreadIdByChannelMessageId(@NonNull Long channelMessageId);
}
