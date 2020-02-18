package jm;


import jm.api.dao.WorkspaceUserRoleDAO;
import jm.model.Role;
import jm.model.WorkspaceUserRole;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WorkspaceUserRoleServiceImpl implements WorkspaceUserRoleService {

    private WorkspaceUserRoleDAO workspaceUserRoleDAO;

    public WorkspaceUserRoleServiceImpl(WorkspaceUserRoleDAO workspaceUserRoleDAO) {
        this.workspaceUserRoleDAO = workspaceUserRoleDAO;
    }

    @Override
    public void create(WorkspaceUserRole workspaceUserRole) {
        workspaceUserRoleDAO.persist(workspaceUserRole);
    }

    @Override
    public Set<Role> getRole(Long workspaceId, Long userId) {
        return workspaceUserRoleDAO.getRole(workspaceId,userId);
    }
}
