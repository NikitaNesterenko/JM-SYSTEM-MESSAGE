package jm;

import jm.model.Role;
import jm.model.Workspace;
import jm.model.WorkspaceUserRole;
import jm.model.User;

import java.util.Set;

public interface WorkspaceUserRoleService {
    void create(WorkspaceUserRole workspaceUserRole);
    Set<Role> getRole(Workspace workspace, User user);
}
