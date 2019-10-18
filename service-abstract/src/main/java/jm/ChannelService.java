package jm;

import jm.model.Channel;
import jm.model.User;

import java.util.List;

public interface ChannelService {
    List<Channel> gelAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Long id);

    void updateChannel(Channel channel);

    Channel getChannelById(Long id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);

}
