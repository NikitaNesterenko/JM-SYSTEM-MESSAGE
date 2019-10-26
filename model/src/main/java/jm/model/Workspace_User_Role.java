package jm.model;

import javax.persistence.*;
import java.util.Objects;

//@Entity
//@Table
//public class Workspace_User_Role {
//
//    @Id
//    @Column(name = "id", nullable = false, unique = true, updatable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(targetEntity = Workspace.class)
//    @JoinColumn(name = "workspace_id")
//    private Workspace workspace;
//
//    @ManyToOne(targetEntity = User.class)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(targetEntity = Role.class)
//    @JoinColumn(name = "role_id")
//    private Role role;
//
//    public Workspace_User_Role() {
//    }
//
//    public Workspace_User_Role(Workspace workspace, User user, Role role) {
//        this.workspace = workspace;
//        this.user = user;
//        this.role = role;
//    }
//
//    public Long getId() {
//        return id;
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
//    public void setId(Long id) {
//        this.id = id;
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
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Workspace_User_Role that = (Workspace_User_Role) o;
//        return Objects.equals(id, that.id) &&
//                Objects.equals(workspace, that.workspace) &&
//                Objects.equals(user, that.user) &&
//                Objects.equals(role, that.role);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, workspace, user, role);
//    }
//
//    @Override
//    public String toString() {
//        return "Workspace_User_Role{" +
//                "id=" + id +
//                ", workspace=" + workspace +
//                ", user=" + user +
//                ", role=" + role +
//                '}';
//    }
//}
