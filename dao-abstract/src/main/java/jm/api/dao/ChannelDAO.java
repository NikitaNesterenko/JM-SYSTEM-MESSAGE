package jm.api.dao;

import jm.model.Channel;
import jm.model.ChannelDTO;
import jm.model.User;

import java.util.List;

public interface ChannelDAO {

    List<Channel> getAll();

    void persist(Channel channel);

    void deleteById(Long id);

    Channel merge(Channel channel);

    Channel getById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);

    List<ChannelDTO> getChannelByWorkspaceAndUser(String workspaceName, String login);

    List<Channel> getChannelByWorkspace(Long workspaceId);

    List<Channel> getChannelByWorkspaceForLastMonth(Long workspaceId);

    List<Channel> getChannelsByWorkspaceId(Long id);

}
