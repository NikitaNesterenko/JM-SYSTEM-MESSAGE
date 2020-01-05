package jm.dao;


import jm.api.dao.WorkspaceUserRoleDAO;
import jm.model.Role;
import jm.model.Workspace;
import jm.model.WorkspaceUserRole;
import jm.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class WorkspaceUserRoleDAOImpl extends AbstractDao<WorkspaceUserRole> implements WorkspaceUserRoleDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Set<Role> getRole(Workspace workspace, User user) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("FROM WorkspaceUserRole WHERE workspace=:workspace AND user=:user")
                .setParameter("workspace", workspace)
                .setParameter("user", user)
                .getResultList();
        Set<Role> roles = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            roles.add(workspaceUserRole.getRole());
        }
        return roles;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<User> getUsersByWorkspace(Workspace workspace) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("FROM WorkspaceUserRole WHERE workspace=:workspace")
                .setParameter("workspace", workspace)
                .getResultList();
        Set<User> users = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            users.add(workspaceUserRole.getUser());
        }
        return users;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Workspace> getWorkspacesByUsers(User user) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("FROM WorkspaceUserRole WHERE user=:user")
                .setParameter("user", user)
                .getResultList();
        Set<Workspace> workspaces = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            workspaces.add(workspaceUserRole.getWorkspace());
        }
        return workspaces;
    }

}
