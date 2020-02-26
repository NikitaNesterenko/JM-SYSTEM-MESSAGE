package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.User;

import java.util.List;

public interface ChannelService {

    List<Channel> gelAllChannels();

    List<Channel> getAllArchiveChannels();

    void unzipChannel(Channel channel);

    void createChannel(Channel channel);

    void deleteChannel(Long id);

    void updateChannel(Channel channel);

    Channel getChannelById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

    List<Channel> getPrivateChannels();
}