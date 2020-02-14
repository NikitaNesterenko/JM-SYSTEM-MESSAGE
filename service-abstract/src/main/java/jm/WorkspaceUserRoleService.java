package jm;

import jm.model.Role;
import jm.model.WorkspaceUserRole;

import java.util.Set;

public interface WorkspaceUserRoleService {

    void create(WorkspaceUserRole workspaceUserRole);

    Set<Role> getRole(Long workspaceId, Long userId);
}
