package jm.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

//@Embeddable
//public class WorkspaceUserRoleId implements Serializable {
//
//    @ManyToOne
//    @JoinColumn(name = "workspaces_id", nullable = false)
//    private Workspace workspace;
//
//    @ManyToOne
//    @JoinColumn(name = "users_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "roles_id", nullable = false)
//    private Role role;
//
//    public WorkspaceUserRoleId() {
//    }
//
//    public WorkspaceUserRoleId(Workspace workspace, User user, Role role) {
//        this.workspace = workspace;
//        this.user = user;
//        this.role = role;
//    }
//
//    public Workspace getWorkspace() {
//        return workspace;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setWorkspace(Workspace workspace) {
//        this.workspace = workspace;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//}
