package jm;

import java.util.Objects;

public class Role {

    private Integer id;
    private String role;

    public Role() {
    }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setId(Integer roleId) {
        this.id = roleId;
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
}
