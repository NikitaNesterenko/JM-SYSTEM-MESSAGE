package jm;

import jm.model.ThreadChannel;

public interface ThreadChannelService {

    void createThreadChannel(ThreadChannel threadChannel);

    ThreadChannel findByChannelMessageId(Long id);
}
