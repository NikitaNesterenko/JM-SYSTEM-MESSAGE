package jm;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.User;
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
        return channelDAO.getAll();
    }

    @Override
    public void createChannel(Channel channel) {
        channelDAO.persist(channel);
    }

    @Override
    public void deleteChannel(Long id) {
        channelDAO.deleteById(id);
    }

    @Override
    public void updateChannel(Channel channel) {
        channelDAO.merge(channel);
    }

    @Override
    public Channel getChannelById(Long id) {
        return channelDAO.getById(id);
    }

    @Override
    public Channel getChannelByName(String name) {
        return channelDAO.getChannelByName(name);
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        return channelDAO.getChannelsByOwner(user);
    }

    @Override
    public List<Channel> getChannelByWorkspaceAndUser(String workspaceName, String login) {
        return channelDAO.getChannelByWorkspaceAndUser(workspaceName, login);
    }
}

