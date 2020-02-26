package jm;

import jm.api.dao.WorkspaceDAO;
import jm.dao.ChannelDAOImpl;
import jm.dto.ChannelDTO;
import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private ChannelDAO channelDAO;
    private UserService userService;
    private WorkspaceDAO workspaceDAO;

    @Autowired
    public ChannelServiceImpl(ChannelDAO channelDAO, UserService userService, WorkspaceDAO workspaceDAO) {
        this.channelDAO = channelDAO;
        this.userService = userService;
        this.workspaceDAO = workspaceDAO;
    }

    public ChannelServiceImpl() {

    }

    @Override
    public List<Channel> gelAllChannels() {
        return channelDAO.getAll();
    }

    @Override
    public List<Channel> getAllArchiveChannels() {
        return channelDAO.getArchivedChannels();
    }

    @Override
    public void createChannel(Channel channel) {
        channelDAO.persist(channel);
    }

    @Override
    public void unzipChannel(Channel channel) {
        channel.setArchived(false);
        updateChannel(channel);
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
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId);
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
        return channelDAO.getChannelsByWorkspaceId(id);
    }

    @Override
    public List<Channel> getChannelsByUserId(Long userId) {
        return channelDAO.getChannelsByUserId(userId);
    }

    @Override
    public List<Channel> getPrivateChannels() {
        return channelDAO.getPrivateChannels();
    }

    public void setChannelDAO(ChannelDAOImpl channelDAO) {
        this.channelDAO = channelDAO;
    }
}