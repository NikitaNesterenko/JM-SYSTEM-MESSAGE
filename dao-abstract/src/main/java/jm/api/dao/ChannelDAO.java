package jm.api.dao;

import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChannelDAO {

    List<Channel> getAll ();

    void persist (Channel channel);

    void deleteById (Long id);

    Channel merge (Channel channel);

    Channel getById (Long id);

    Channel getChannelByName (String name);

    Set<Long> getSetUserIdsByName (String name);

    Set<Long> getSetBotIdsByName (String name);

    List<Channel> getChannelsByOwnerId (Long ownerId);

    List<Channel> getChannelsByWorkspaceId (Long id);

    List<Channel> getChannelsByUserId (Long userId);

    List<Channel> getChannelsByIds (Set<Long> ids);

    Optional<ChannelDTO> getIdByName (String name);

    Optional<ChannelDTO> getChannelDTOByNameWithoutFieldsUserIdsAndBotIds (String name);

    Optional<ChannelDTO> getChannelDTOByIdWithoutFieldsUserIdsAndBotIds (Long id);

    List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId);


}
