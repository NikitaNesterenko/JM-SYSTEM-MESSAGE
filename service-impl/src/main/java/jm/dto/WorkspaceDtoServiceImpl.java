package jm.dto;

import jm.api.dao.ChannelDAO;
import jm.api.dao.UserDAO;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkspaceDtoServiceImpl implements WorkspaceDtoService {
    private final UserDAO userDAO;
    private final ChannelDAO channelDAO;

    public WorkspaceDtoServiceImpl(UserDAO userDAO, ChannelDAO channelDAO) {
        this.userDAO = userDAO;
        this.channelDAO = channelDAO;
    }

    @Override
    public WorkspaceDTO toDto(Workspace workspace) {
        if (workspace == null) {
            return null;
        }
        WorkspaceDTO workspaceDto = new WorkspaceDTO(workspace);
        if (workspace.getUsers() != null) {
            Set<Long> userIds = workspace.getUsers().stream().map(User::getId).collect(Collectors.toSet());
            workspaceDto.setUserIds(userIds);
        }
        if (workspace.getChannels() != null) {
            Set<Long> channelIds = workspace.getChannels().stream().map(Channel::getId).collect(Collectors.toSet());
            workspaceDto.setUserIds(channelIds);
        }
        return workspaceDto;
    }

    @Override
    public Workspace toEntity(WorkspaceDTO workspaceDto) {
        if (workspaceDto == null) {
            return null;
        }
        Workspace workspace = new Workspace(workspaceDto);
        Set<Long> userIds = workspaceDto.getUserIds();
        if (userIds != null) {
            List<User> users = userDAO.getUsersByIds(userIds);
            workspace.setUsers(new HashSet<>(users));
        }
        Set<Long> channelIds = workspaceDto.getChannelIds();
        if (channelIds != null) {
            List<Channel> channels = channelDAO.getChannelsByIds(channelIds);
            workspace.setChannels(new HashSet<>(channels));
        }
        User owner = userDAO.getById(workspaceDto.getOwnerId());
        workspace.setUser(owner);
        return null;
    }
}
