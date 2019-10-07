package jm;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user);

    void createInitUser(User user, String role);

    void deleteUser(User user);

    void updateUser(User user);

    User getUserById(int id);

    User getUserByLogin(String login);

}
