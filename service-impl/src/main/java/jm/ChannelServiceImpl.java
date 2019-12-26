package jm;

import jm.dto.ChannelDTO;
import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<Channel> getChannelByName(String name) { return channelDAO.getChannelByName(name); }

    @Override
    public Optional<List<Channel>> getChannelsByOwner(User user) {
        return channelDAO.getChannelsByOwner(user);
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) { return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId); }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) { return channelDAO.getChannelsByWorkspaceId(id); }

    @Override
    public  List<Channel> getChannelsByUserId(Long userId) {
        return channelDAO.getChannelsByUserId(userId);
    }

}

