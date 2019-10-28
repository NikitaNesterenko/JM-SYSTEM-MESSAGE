package jm.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "workspace_user_role")
public class WorkspaceUserRole {

    @EmbeddedId
    private WorkspaceUserRoleId workspaceUserRoleId = new WorkspaceUserRoleId();

    public WorkspaceUserRole() {
    }

    public WorkspaceUserRole(WorkspaceUserRoleId workspaceUserRoleId) {
        this.workspaceUserRoleId = workspaceUserRoleId;
    }

    public WorkspaceUserRoleId getWorkspaceUserRoleId() {
        return workspaceUserRoleId;
    }

    public void setWorkspaceUserRoleId(WorkspaceUserRoleId workspaceUserRoleId) {
        this.workspaceUserRoleId = workspaceUserRoleId;
    }
}
