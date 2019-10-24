package jm.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false)
    private String role;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Set<WorkspaceUserRoleLink> workspaceUserRoleLink = new HashSet<>();

    public Set<WorkspaceUserRoleLink> getWorkspaceUserRoleLink() {
        return this.workspaceUserRoleLink;
    }

    public void setWorkspaceUserRoleLink(Set<WorkspaceUserRoleLink> workspaceUserRoleLink) {
        this.workspaceUserRoleLink = workspaceUserRoleLink;
    }

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(String role) {

        this.role = role;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role1 = (Role) o;
        return id.equals(role1.id) &&
                role.equals(role1.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }

    @JsonIgnore
    @Override
    public String getAuthority() {
        return this.role;
    }

}
