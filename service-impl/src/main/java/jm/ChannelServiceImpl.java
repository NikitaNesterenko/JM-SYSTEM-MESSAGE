package jm;

import jm.api.dao.ChannelDAO;
import jm.dao.ChannelDAOImpl;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private ChannelDAO channelDAO;
    private ChannelDTO channelDTO;
    private ChannelDAOImpl channelDaoImpl;

    @Autowired
    public void setChannelDAO(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @Override
    public List<ChannelDTO> getAllChannels() {
       return channelDAO.getAllChannel();
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
    public List<Channel> getChannelsByOwnerId(Long ownerId) {
        return channelDAO.getChannelsByOwnerId(ownerId);
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId);
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) { return channelDAO.getChannelsByWorkspaceId(id); }

    @Override
    public  List<Channel> getChannelsByUserId(Long userId) {
        return channelDAO.getChannelsByUserId(userId);
    }

    @Override
    public String getTopicChannelByChannelId(Long id) {
        return channelDAO.getTopicChannelByChannelId(id);
    }

    @Override
    public Long getWorkspaceIdByChannelId(Long channelId) {
        return channelDAO.getWorkspaceIdByChannelId(channelId);
    }

    @Override
    public List<ChannelDTO> getAllArchiveChannels() {
        return channelDAO.getArchivedChannels();
    }

    @Override
    public List<ChannelDTO> getPrivateChannels() {
        return channelDAO.getPrivateChannels();
    }

    @Override
    public void unzipChannel(Channel channel) {
        channel.setArchived(false);
        channelDAO.unzipChannel(channel.getId());
    }
}

