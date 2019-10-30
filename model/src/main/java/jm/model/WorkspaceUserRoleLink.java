package jm.model;


import javax.persistence.*;

@Entity
@Table(name = "workspaces_users_roles")
public class WorkspaceUserRoleLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id")
    private Role role;

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        workspace.getWorkspaceUserRoleLink().add(this);

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user.getWorkspaceUserRoleLink().add(this);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        role.getWorkspaceUserRoleLink().add(this);
    }

    public WorkspaceUserRoleLink() {

    }
}
