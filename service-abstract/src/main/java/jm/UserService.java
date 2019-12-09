package jm;

import jm.dto.UserDTO;
import jm.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User getUserById(Long id);

    User getUserByLogin(String login);

    User getUserByEmail(String email);

    List<User> getAllUsersInThisChannel(Long id);

    User getUserByName(String name);

    List<UserDTO> getAllUsersInWorkspace(Long id);

}
