package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    List<Channel> gelAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Long id);

    void updateChannel(Channel channel);

    Channel getChannelById(Long id);

    Optional<Channel> getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

}
