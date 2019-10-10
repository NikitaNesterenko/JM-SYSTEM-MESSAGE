package jm.api.dao;

import jm.model.Channel;
import jm.model.User;

import java.util.List;

public interface ChannelDAO {

    List<Channel> getAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Channel channel);

    void updateChannel(Channel channel);

    Channel getChannelById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);
}
