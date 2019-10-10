package jm.api.dao;

import jm.model.Role;

public interface RoleDAO {
    void addRole(String role);

    Role getRole(String role);
}
