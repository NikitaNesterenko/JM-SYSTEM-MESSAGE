package jm.api.dao;

import jm.dto.UserDTO;
import jm.model.User;
import jm.model.message.DirectMessage;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDAO {

    List<User> getAll();

    void persist(User user);

    void deleteById(Long id);

    Boolean deleteUser(@NonNull User user);

    User merge(User user);

    Boolean updateUser(@NonNull User user);

    User getById(Long id);

    User getUserByLogin(String login);

    User getUserByName(String name);

    User getUserByEmail(String email);

    void addRoleForUser(User user, String role);

    void updateUserRole(User user, String role);

    List<User> getAllUsersInThisChannel(Long id);

    List<UserDTO> getUsersInWorkspace(Long id);

    List<User> getUsersByIds(Set<Long> ids);

    boolean isEmailInThisWorkspace(String email, Long workspaceId);

    Optional<List<UserDTO>> getAllUsersDTO();

    Optional<UserDTO> getUserDTOById(Long id);

    Optional<UserDTO> getUserDTOByLogin(String login);

    Optional<UserDTO> getUserDTOByEmail(String email);

    Optional<List<UserDTO>> getAllUsersDTOInThisChannel(Long id);

    Optional<String> getLoginBuUserName(String userName);

    Optional<String> getLoginByMessageId(@NonNull Long messageId);

    Optional<String> getLoginByUserId(@NonNull Long userId);
}
