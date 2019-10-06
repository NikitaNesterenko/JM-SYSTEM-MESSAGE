package jm;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user, String role);

    void createUser2(User user);

    boolean deleteUser(User user);

    void updateUser(User user, String role);

    void updateUser2(User user);

    User getUserById(int id);

    User getUserByLogin(String login);

}
