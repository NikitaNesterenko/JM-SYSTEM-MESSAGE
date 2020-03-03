package jm.api.dao;

import jm.model.User;
import java.util.List;
import java.util.Set;

public interface UserDAO {

    List<User> getAll();

    void persist(User user);

    void deleteById(Long id);

    User merge(User user);

    User getById(Long id);

    User getUserByLogin(String login);

    User getUserByName(String name);

    User getUserByEmail(String email);

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

    List<User> getAllUsersByChannel(Long channelID);

    List<User> getAllUsersByWorkspace(Long workspaceID);

    List<User> getUsersByIDs(Set<Long> userIDs);
}
