package jm.dao;


import jm.api.dao.WorkspaceUserRoleDAO;
import jm.model.Role;
import jm.model.Workspace;
import jm.model.WorkspaceUserRole;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class WorkspaceUserRoleDAOImpl extends AbstractDao<WorkspaceUserRole> implements WorkspaceUserRoleDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Set<Role> getRole(Workspace workspace, User user) {
        List<WorkspaceUserRole> userWorkspacesRoles = entityManager
                .createQuery("from WorkspaceUserRole where workspace = :workspace and user=:user")
                .setParameter("workspace", workspace)
                .setParameter("user", user)
                .getResultList();
        Set<Role> roles = new HashSet<>();
        for (WorkspaceUserRole userRole: userWorkspacesRoles) {
            roles.add(userRole.getRole());
        }
        return roles;
    }

}
