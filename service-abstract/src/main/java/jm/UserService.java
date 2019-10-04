package jm;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user, String role);

    void deleteUser(User user);

    void updateUser(User user, String role);

    User getUserById(int id);

    User getUserByLogin(String login);

}
