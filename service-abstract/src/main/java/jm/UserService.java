package jm;

import jm.dto.UserDTO;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User getUserById(Long id);

    Optional<User> getUserByLogin(String login);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsersInThisChannel(Long id);

    List<UserDTO> getAllUsersInWorkspace(Long id);

}
