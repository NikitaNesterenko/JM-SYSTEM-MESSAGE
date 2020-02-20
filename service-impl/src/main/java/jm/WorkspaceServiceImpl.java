package jm;

import jm.api.dao.WorkspaceDAO;
import jm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    private WorkspaceDAO workspaceDAO;

    private CreateWorkspaceTokenService createWorkspaceTokenService;

    private UserService userService;

    private WorkspaceUserRoleService workspaceUserRoleService;

    @Autowired
    public void setCreateWorkspaceTokenService(CreateWorkspaceTokenService createWorkspaceTokenService) {
        this.createWorkspaceTokenService = createWorkspaceTokenService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setWorkspaceUserRoleService(WorkspaceUserRoleService workspaceUserRoleService) {
        this.workspaceUserRoleService = workspaceUserRoleService;
    }

    @Autowired
    public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public List<Workspace> gelAllWorkspaces() {
        return workspaceDAO.getAll();
    }

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
    }

    @Override
    public Workspace getWorkspaceByName(String name) { return workspaceDAO.getWorkspaceByName(name); }

    @Override
    public List<Workspace> getWorkspacesByOwnerId(Long ownerId) { return workspaceDAO.getWorkspacesByOwnerId(ownerId);}

    @Override
    public List<Workspace> getWorkspacesByUserId(Long userId) {
        return workspaceDAO.getWorkspacesByUserId(userId);
    }

    @Override
    public void createWorkspaceByToken(CreateWorkspaceToken createWorkspaceToken) {
        Set<User> users = null;
        createWorkspaceTokenService.updateCreateWorkspaceToken(createWorkspaceToken);
        User emailUser = userService.getUserByLogin(createWorkspaceToken.getUserEmail());
        users.add(emailUser);
        Workspace workspace1 = new Workspace(createWorkspaceToken.getWorkspaceName(),users, emailUser,false, LocalDateTime.now());
        createWorkspace(workspace1);
        workspace1 = getWorkspaceByName(createWorkspaceToken.getWorkspaceName());
        workspaceUserRoleService.create(new WorkspaceUserRole(
                workspace1,
                emailUser,
                new Role( 2L, "ROLE_OWNER")));
    }

}
