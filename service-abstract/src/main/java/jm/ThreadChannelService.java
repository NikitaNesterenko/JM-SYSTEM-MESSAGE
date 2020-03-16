package jm;

import jm.dto.MessageDTO;
import jm.model.Message;
import jm.model.ThreadChannel;

public interface ThreadChannelService {

    void createThreadChannel(ThreadChannel threadChannel);

    ThreadChannel createThreadChannelByMessageDTO(MessageDTO messageDTO);

    ThreadChannel findByChannelMessageId(Long id);
}
