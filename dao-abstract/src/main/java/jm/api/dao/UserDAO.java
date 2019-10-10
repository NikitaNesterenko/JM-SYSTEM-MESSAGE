package jm.api.dao;

import jm.model.User;

import java.util.List;

public interface UserDAO {

    List<User> getAllUsers();

    void createUser(User user);

    void deleteUser(User user);

    void updateUser(User user);

    User getUserById(int id);

    User getUserByLogin(String login);

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

}
