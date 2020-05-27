package jm;

import jm.dto.AssociatedUserDTO;
import jm.dto.UserDTO;
import jm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers(); //+

    void createUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User getUserById(Long id); //+


    User getUserByLogin(String login); //+

    User getUserByName(String name);

    User getUserByEmail(String email); //+

    User createUserByEmail(String email);

    List<User> getAllUsersInChannelByChannelId(Long id); //+

    List<UserDTO> getAllUsersInWorkspaceByWorkspaceId(Long id);

    void removeChannelMessageFromUnreadForUser(Long channelId, Long userId);

    void removeDirectMessagesForConversationFromUnreadForUser(Long conversationId, Long userId);

    boolean isEmailInThisWorkspace(String email, Long workspaceId);

    Optional<List<UserDTO>> getAllUsersDTO();

    Optional<UserDTO> getUserDTOById(Long id);

    Optional<UserDTO> getUserDTOByLogin(String login);

    Optional<UserDTO> getUserDTOByName(String name);

    Optional<UserDTO> getUserDTOByEmail(String email);

    Optional<List<UserDTO>> getAllUsersDTOInChannelByChannelId(Long id);

    User getEntityFromDTO(UserDTO userDTO);

    Optional<List<AssociatedUserDTO>> getAllAssociatedUserDTOinWorkspaceByWorkspaceId(Long id);
}
