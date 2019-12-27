package jm.api.dao;

import jm.dto.UserDTO;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> getAll();

    void persist(User user);

    void deleteById(Long id);

    User merge(User user);

    User getById(Long id);

    Optional<User> getUserByLogin(String login);

    Optional<User> getUserByEmail(String email);

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

    List<User> getAllUsersInThisChannel(Long id);

    List<UserDTO> getUsersInWorkspace(Long id);
}
