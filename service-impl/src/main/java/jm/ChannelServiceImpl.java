package jm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private ChannelDAO channelDAO;

    @Autowired
    public void setChannelDAO(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @Override
    public List<Channel> gelAllChannels() {
        return channelDAO.getAllChannels();
    }

    @Override
    public void createChannel(Channel channel) {
        channelDAO.createChannel(channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        channelDAO.deleteChannel(channel);
    }

    @Override
    public void updateChannel(Channel channel) {
        channelDAO.updateChannel(channel);
    }

    @Override
    public Channel getChannelById(int id) {
        return channelDAO.getChannelById(id);
    }

    @Override
    public Channel getChannelByName(String name) {
        return channelDAO.getChannelByName(name);
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        return channelDAO.getChannelsByOwner(user);
    }
}

