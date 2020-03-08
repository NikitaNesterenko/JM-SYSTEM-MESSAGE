package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.List;

public interface ChannelService {
    List<ChannelDTO> getAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Long id);

    void updateChannel(Channel channel);

    Channel getChannelById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwnerId(Long ownerId);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

    Long getWorkspaceIdByChannelId(Long channelId);

    List<Channel> getAllArchiveChannels();

    List<Channel> getPrivateChannels();

    void unzipChannel(Channel channel);
}
