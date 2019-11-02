package jm.api.dao;


import jm.model.Role;
import jm.model.Workspace;
import jm.model.WorkspaceUserRole;
import jm.model.User;

import java.util.List;
import java.util.Set;

public interface WorkspaceUserRoleDAO {
    List<WorkspaceUserRole> getAll();

    void persist(WorkspaceUserRole workspaceUserRole);

    void deleteById(Long id);

    WorkspaceUserRole merge(WorkspaceUserRole workspaceUserRole);

    WorkspaceUserRole getById(Long id);

    Set<Role> getRole(Workspace workspace, User user);

    Set<User> getUsersByWorkspace(Workspace workspace);

    Set<Workspace> getWorkspacesByUsers(User user);

}
