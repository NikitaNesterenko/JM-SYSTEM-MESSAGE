package jm;

import jm.dto.ThreadDTO;
import jm.model.ThreadChannel;
import lombok.NonNull;

import java.util.Optional;

public interface ThreadChannelService {

    void createThreadChannel(ThreadChannel threadChannel);

    ThreadChannel findByChannelMessageId(Long id);

    Optional<ThreadDTO> getThreadDtoByChannelMessageId(@NonNull Long channelMessageId);
}
