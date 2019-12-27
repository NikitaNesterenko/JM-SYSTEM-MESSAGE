package jm.api.dao;

import jm.model.Channel;
import jm.dto.ChannelDTO;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface ChannelDAO {

    List<Channel> getAll();

    void persist(Channel channel);

    void deleteById(Long id);

    Channel merge(Channel channel);

    Channel getById(Long id);

    Optional<Channel> getChannelByName(String name);

    Optional<List<Channel>> getChannelsByOwner(User user);

    List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId);

    List<Channel> getChannelsByWorkspaceId(Long id);

    List<Channel> getChannelsByUserId(Long userId);

}
