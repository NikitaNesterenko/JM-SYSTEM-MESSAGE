package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    List<Channel> gelAllChannels ();

    void createChannel (Channel channel);

    void deleteChannel (Long id);

    void updateChannel (Channel channel);

    Channel getChannelById (Long id);

    Optional<ChannelDTO> getChannelDTOById (Long id);

    Channel getChannelByName (String name);

    Optional<ChannelDTO> getChannelDTOByName (String name);

    List<Channel> getChannelsByOwnerId (Long ownerId);

    List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId (Long id);

    List<Channel> getChannelsByUserId (Long userId);

}
