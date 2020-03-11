package jm;

import jm.api.dao.ChannelDAO;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.dto.WorkspaceDTO;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    private final WorkspaceDAO workspaceDAO;
    private final UserDAO userDAO;
    private final ChannelDAO channelDAO;

    @Autowired
    public WorkspaceServiceImpl(WorkspaceDAO workspaceDAO, UserDAO userDAO, ChannelDAO channelDAO) {
        this.workspaceDAO = workspaceDAO;
        this.userDAO = userDAO;
        this.channelDAO = channelDAO;
    }

    @Override
    public List<Workspace> getAllWorkspaces() {
        return workspaceDAO.getAll();
    } //+

    @Override
    public void createWorkspace(Workspace workspace) {
        workspaceDAO.persist(workspace);
    }

    @Override
    public void deleteWorkspace(Long id) {
        workspaceDAO.deleteById(id);
    }

    @Override
    public void updateWorkspace(Workspace workspace) {
        workspaceDAO.merge(workspace);
    }

    @Override
    public Workspace getWorkspaceById(Long id) {
        return workspaceDAO.getById(id);
    } //+

    @Override
    public Workspace getWorkspaceByName(String name) { return workspaceDAO.getWorkspaceByName(name); }

    @Override
    public List<Workspace> getWorkspacesByOwnerId(Long ownerId) { return workspaceDAO.getWorkspacesByOwnerId(ownerId);}

    @Override
    public List<Workspace> getWorkspacesByUserId(Long userId) {
        return workspaceDAO.getWorkspacesByUserId(userId);
    } //+

    @Override
    public Optional<List<WorkspaceDTO>> getAllWorkspacesDTO() {
        return workspaceDAO.getAllWorkspacesDTO();
    }

    @Override
    public Optional<WorkspaceDTO> getWorkspaceDTOById(Long id) {
        return workspaceDAO.getWorkspaceDTOById(id);
    }

    @Override
    public Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId) {
        return workspaceDAO.getWorkspacesDTOByUserId(userId);
    }

    @Override
    public Workspace getEntityFromDTO(WorkspaceDTO workspaceDto) {
        if (workspaceDto == null) {
            return null;
        }

        // creating new Workspace with simple fields copied from WorkspaceDTO
        Workspace workspace = new Workspace(workspaceDto);

        // setting up 'users'
        Set<Long> userIds = workspaceDto.getUserIds();
        if (userIds != null) {
            List<User> users = userDAO.getUsersByIds(userIds);
            workspace.setUsers(new HashSet<>(users));
        }

        // setting up 'channels'
        Set<Long> channelIds = workspaceDto.getChannelIds();
        if (channelIds != null) {
            List<Channel> channels = channelDAO.getChannelsByIds(channelIds);
            workspace.setChannels(new HashSet<>(channels));
        }

        // setting up 'user'
        User owner = userDAO.getById(workspaceDto.getOwnerId());
        workspace.setUser(owner);
        return null;
    }


}
