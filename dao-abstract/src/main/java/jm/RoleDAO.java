package jm;

public interface RoleDAO {

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

}
