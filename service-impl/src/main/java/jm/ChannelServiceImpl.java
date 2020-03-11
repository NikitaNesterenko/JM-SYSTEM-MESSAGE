package jm;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
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
    public void setChannelDAO (ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @Override
    public List<Channel> gelAllChannels () {
        return channelDAO.getAll();
    }

    @Override
    public List<ChannelDTO> getAllChanelDTO () {
        return channelDAO.getAllChanelDTO();
    }

    @Override
    public void createChannel (Channel channel) {
        channelDAO.persist(channel);
    }

    @Override
    public void deleteChannel (Long id) {
        channelDAO.deleteById(id);
    }

    @Override
    public void updateChannel (Channel channel) {
        channelDAO.merge(channel);
    }

    @Override
    public Channel getChannelById (Long id) {
        return channelDAO.getById(id);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOById (Long id) {
        return channelDAO.getChannelDTOById(id);
    }

    @Override
    public Channel getChannelByName (String name) {
        return channelDAO.getChannelByName(name);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByName (String name) {
        return channelDAO.getChannelDTOByName(name);
    }

    @Override
    public Optional<Long> getChannelIdByName(String chanelName) {
        return channelDAO.getChannelIdByName(chanelName);
    }

    @Override
    public List<Channel> getChannelsByOwnerId (Long ownerId) {
        return channelDAO.getChannelsByOwnerId(ownerId);
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId) {
        return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId);
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId (Long id) {
        return channelDAO.getChannelsByWorkspaceId(id);
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByWorkspaceId (Long workspaceId) {
        return channelDAO.getChannelDtoListByWorkspaceId(workspaceId);
    }

    @Override
    public List<Channel> getChannelsByUserId (Long userId) {
        return channelDAO.getChannelsByUserId(userId);
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByUserId (Long userId) {
        return channelDAO.getChannelDtoListByUserId(userId);
    }
}

