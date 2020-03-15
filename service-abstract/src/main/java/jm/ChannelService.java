package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;
import lombok.NonNull;
import jm.model.CreateWorkspaceToken;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    // TODO: проверить
    //List<ChannelDTO> getAllChannels();
    List<Channel> gelAllChannels();

    List<ChannelDTO> getAllChanelDTO ();

    void createChannel (Channel channel);

    void deleteChannel (Long id);

    void updateChannel (Channel channel);

    void createChannelByTokenAndUsers(CreateWorkspaceToken createWorkspaceToken, Set<User> users);

    Channel getChannelById (Long id);

    Optional<ChannelDTO> getChannelDTOById (Long id);

    Channel getChannelByName (String name);

    Optional<Long> getChannelIdByName(String chanelName);

    Optional<ChannelDTO> getChannelDTOByName (String name);

    List<Channel> getChannelsByOwnerId (Long ownerId);

    List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId (Long workspaceId);

    List<ChannelDTO> getChannelDtoListByWorkspaceId (Long id);

    List<Channel> getChannelsByUserId (Long userId);

    List<ChannelDTO> getChannelDtoListByUserId (Long userId);

    List<ChannelDTO> getChannelDtoListByChannelList (@NonNull List<Channel> channelList);

    Channel getChannelByChannelDto(@NonNull ChannelDTO channelDTO);

    ChannelDTO getChannelDtoByChannel(@NonNull Channel channel);

    String getTopicChannelByChannelId(Long id);

    Long getWorkspaceIdByChannelId(Long channelId);

    List<ChannelDTO> getAllArchiveChannels();

    List<ChannelDTO> getPrivateChannels();

    void unzipChannel(Channel channel);
}
