package jm.api.dao;

import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChannelDAO {

    List<Channel> getAll ();

    // TODO: ПРОВЕРИТЬ
    // List<ChannelDTO> getAllChannel();
    List<ChannelDTO> getAllChanelDTO ();

    void persist (Channel channel);

    void deleteById (Long id);

    Channel merge (Channel channel);

    Channel getById (Long id);

    //Optional<Channel> getChannelByName (String name);
    Channel getChannelByName (String name);

    Optional<Long> getChannelIdByName (String chanelName);

    List<Channel> getChannelsByOwnerId (Long ownerId);

    List<Channel> getChannelsByWorkspaceId (Long id);

    List<ChannelDTO> getChannelDtoListByWorkspaceId (Long workspaceId);

    List<Channel> getChannelsByUserId (Long userId);

    List<ChannelDTO> getChannelDtoListByUserId (Long userId);

    List<Channel> getChannelsByIds (Set<Long> ids);

    Optional<ChannelDTO> getIdByName (String name);

    Optional<ChannelDTO> getChannelDTOByName (String name);

    Optional<ChannelDTO> getChannelDTOById (Long id);

    List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId);


    String getTopicChannelByChannelId(Long id);

    Workspace getWorkspaceByChannelId(Long channelId);

    List<ChannelDTO> getArchivedChannels();

    List<ChannelDTO> getPrivateChannels();

    Channel unzipChannel(Long id);
}
