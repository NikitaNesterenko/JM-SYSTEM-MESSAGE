package jm;

import jm.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user);

    void deleteUser(User user);

    void updateUser(User user);

    User getUserById(Long id);

    User getUserByLogin(String login);

}
