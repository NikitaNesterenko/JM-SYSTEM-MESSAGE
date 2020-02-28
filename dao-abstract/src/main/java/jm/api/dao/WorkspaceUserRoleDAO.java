package jm.api.dao;


import jm.model.Role;
import jm.model.Workspace;
import jm.model.WorkspaceUserRole;
import jm.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkspaceUserRoleDAO {
    List<WorkspaceUserRole> getAll();

    void persist(WorkspaceUserRole workspaceUserRole);

    void deleteById(Long id);

    WorkspaceUserRole merge(WorkspaceUserRole workspaceUserRole);

    WorkspaceUserRole getById(Long id);

    Set<Role> getRole(Long workspaceId, Long userId);

    Set<User> getUsersByWorkspaceId(Long workspaceId);

    Set<Workspace> getWorkspacesByUserId(Long userId);

}
