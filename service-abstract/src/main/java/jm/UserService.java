package jm;

import jm.dto.UserDTO;
import jm.model.User;
import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    void createUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User getUserById(Long id);

    User getUserByLogin(String login);

    User getUserByName(String name);

    User getUserByEmail(String email);

    List<User> getAllUsersByChannel(Long channelID);

    List<User> getAllUsersByWorkspace(Long workspaceID);

    List<User> getUsersByIDs(Set<Long> userIDs);
}
