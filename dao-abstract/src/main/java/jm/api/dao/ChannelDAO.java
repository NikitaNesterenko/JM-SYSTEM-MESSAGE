package jm.api.dao;

import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.List;
import java.util.Set;

public interface ChannelDAO {

    List<ChannelDTO> getAllChannel();

    void persist(Channel channel);

    void deleteById(Long id);

    Channel merge(Channel channel);

    Channel getById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwnerId(Long ownerId);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

    List<Channel> getChannelsByIds(Set<Long> ids);

    String getTopicChannelByChannelId(Long id);

    Long getWorkspaceIdByChannelId(Long channelId);

    List<ChannelDTO> getArchivedChannels();

    List<ChannelDTO> getPrivateChannels();

    Channel unzipChannel(Long id);
}
