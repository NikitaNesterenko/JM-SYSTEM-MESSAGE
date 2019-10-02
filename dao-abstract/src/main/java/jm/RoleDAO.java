package jm;

public interface RoleDAO {

    void addRole(String role);

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

}
