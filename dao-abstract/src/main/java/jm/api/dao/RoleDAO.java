package jm.api.dao;

import jm.model.Role;

import java.util.List;

public interface RoleDAO {

    List<Role> getAll();

    void persist(Role role);

    void deleteById(Long id);

    Role merge(Role role);

    Role getById(Long id);

    Role getRoleByRolename(String role);

}
