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
    public Set<Role> getRole(Long workspaceId, Long userId) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("from WorkspaceUserRole where workspace.id = :workspaceId and user.id=:userId")
                .setParameter("workspaceId", workspaceId)
                .setParameter("userId", userId)
                .getResultList();
        Set<Role> roles = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            roles.add(workspaceUserRole.getRole());
        }
        return roles;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<User> getUsersByWorkspaceId(Long workspaceId) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("from WorkspaceUserRole where workspace.id = :workspace")
                .setParameter("workspace", workspaceId)
                .getResultList();
        Set<User> users = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            users.add(workspaceUserRole.getUser());
        }
        return users;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Workspace> getWorkspacesByUserId(Long userId) {
        List<WorkspaceUserRole> workspaceUserRoles = entityManager
                .createQuery("from WorkspaceUserRole where user.id=:user")
                .setParameter("user", userId)
                .getResultList();
        Set<Workspace> workspaces = new HashSet<>();
        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
            workspaces.add(workspaceUserRole.getWorkspace());
        }
        return workspaces;
    }

}
