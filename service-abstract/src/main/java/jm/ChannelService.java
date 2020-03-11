package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.CreateWorkspaceToken;
import jm.model.User;

import java.util.List;

public interface ChannelService {
    List<ChannelDTO> getAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Long id);

    void updateChannel(Channel channel);

    void createChannelByTokenAndUsers(CreateWorkspaceToken createWorkspaceToken, Set<User> users);

    Channel getChannelById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwnerId(Long ownerId);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

    Long getWorkspaceIdByChannelId(Long channelId);

    List<ChannelDTO> getAllArchiveChannels();

    List<ChannelDTO> getPrivateChannels();

    void unzipChannel(Channel channel);
}
