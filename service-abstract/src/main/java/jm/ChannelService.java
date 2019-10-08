package jm;

import java.util.List;

public interface ChannelService {
    List<Channel> gelAllChannels();

    void createChannel(Channel channel);

    void deleteChannel(Channel channel);

    void updateChannel(Channel channel);

    Channel getChannelById(int id);

    Channel getChannelByName(String name);

    List<Channel> getChannelsByOwner(User user);

}
