package jm.dto;

import jm.api.dao.ChannelDAO;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
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
    private final WorkspaceDAO messageDAO;
    private final ChannelDAO channelDAO;

    public WorkspaceDtoServiceImpl(UserDAO userDAO, WorkspaceDAO messageDAO, ChannelDAO channelDAO) {
        this.userDAO = userDAO;
        this.messageDAO = messageDAO;
        this.channelDAO = channelDAO;
    }

    @Override
    public WorkspaceDTO toDto(Workspace workspace) {

        if (workspace == null) {
            return null;
        }

        WorkspaceDTO workspaceDto = new WorkspaceDTO();

        workspaceDto.setId(workspace.getId());
        workspaceDto.setName(workspace.getName());

        Set<Long> userIds = workspace.getUsers().stream().map(User::getId).collect(Collectors.toSet());
        workspaceDto.setUserIds(userIds);

        Set<Long> channelIds = workspace.getChannels().stream().map(Channel::getId).collect(Collectors.toSet());
        workspaceDto.setUserIds(channelIds);

        workspaceDto.setOwnerId(workspace.getUser().getId());

        workspaceDto.setIsPrivate(workspace.getIsPrivate());
        workspace.setCreatedDate(workspace.getCreatedDate());

        return workspaceDto;
    }

    @Override
    public Workspace toEntity(WorkspaceDTO workspaceDto) {
        if (workspaceDto == null) {
            return null;
        }

        Workspace workspace = new Workspace();

        workspace.setId(workspaceDto.getId());
        workspace.setName(workspaceDto.getName());

        Set<Long> userIds = workspaceDto.getUserIds();
        List<User> users = userDAO.getUsersByIds(userIds);
        workspace.setUsers(new HashSet<>(users));

        Set<Long> channelIds = workspaceDto.getUserIds();
        List<Channel> channels = channelDAO.getChannelsByIds(channelIds);
        workspace.setChannels(new HashSet<>(channels));

        User owner = userDAO.getById(workspaceDto.getOwnerId());
        workspace.setUser(owner);

        workspace.setIsPrivate(workspaceDto.getIsPrivate());
        workspace.setCreatedDate(workspaceDto.getCreatedDate());

        return null;
    }
}
