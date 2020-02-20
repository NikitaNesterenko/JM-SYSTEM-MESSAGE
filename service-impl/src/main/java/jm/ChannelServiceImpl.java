package jm;

import jm.dto.ChannelDTO;
import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.CreateWorkspaceToken;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private ChannelDAO channelDAO;

    private CreateWorkspaceTokenService createWorkspaceTokenService;

    private WorkspaceService workspaceService;

    private UserService userService;

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCreateWorkspaceTokenService(CreateWorkspaceTokenService createWorkspaceTokenService) {
        this.createWorkspaceTokenService = createWorkspaceTokenService;
    }

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
    public void createChannelByTokenAndUsers(CreateWorkspaceToken createWorkspaceToken, Set<User> users) {
        createWorkspaceTokenService.updateCreateWorkspaceToken(createWorkspaceToken);
        Workspace workspace = workspaceService.getWorkspaceByName(createWorkspaceToken.getWorkspaceName());
        Channel channel = new Channel(
                createWorkspaceToken.getChannelname(),
                users,
                userService.getUserByEmail(createWorkspaceToken.getUserEmail()),
                false,
                LocalDateTime.now(),workspace);
        createChannel(channel);
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

}

