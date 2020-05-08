package jm;

import jm.api.dao.DirectMessageDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.dto.UserDTO;
import jm.model.Message;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private final MessageDAO messageDAO;
    private final DirectMessageDAO directMessageDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, MessageDAO messageDAO, DirectMessageDAO directMessageDAO) {
        this.userDAO = userDAO;
        this.messageDAO = messageDAO;
        this.directMessageDAO = directMessageDAO;
    }


    @Override
    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    @Override
    public void createUser(User user) {
        userDAO.persist(user);
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void deleteUser(Long id) {
        userDAO.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
        userDAO.merge(user);
    }

    @Override
    public User getUserById(Long id) {
        return userDAO.getById(id);
    }

    @Override
    public User getUserByLogin(String login) {
        return userDAO.getUserByLogin(login).get();
    }

    @Override
    public User getUserByName(String name) {
        return userDAO.getUserByName(name).get();
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email).get();
    }

    @Override
    public User createUserByEmail(String email) {
        User user = new User(email, email, email, email, email);
        createUser(user);
        return user;
    }

    @Override
    public List<User> getAllUsersInChannelByChannelId(Long id) {
        return userDAO.getAllUsersInThisChannelByChannelId(id);
    }

    @Override
    public List<UserDTO> getAllUsersInWorkspaceByWorkspaceId(Long id) {
        return userDAO.getUsersInWorkspace(id);
    }

    @Override
    public void removeChannelMessageFromUnreadForUser(Long channelId, Long userId) {
        User user = userDAO.getById(userId);
        user.getUnreadMessages().removeIf(msg -> msg.getChannelId().equals(channelId));
        this.updateUser(user);
    }

    @Override
    public void removeDirectMessagesForConversationFromUnreadForUser(Long conversationId, Long userId) {
        User user = this.getUserById(userId);
        user.getUnreadDirectMessages().removeIf(dmsg -> dmsg.getConversation().getId().equals(conversationId));
        this.updateUser(user);
    }

    @Override
    public boolean isEmailInThisWorkspace(String email, Long workspaceId) {
        return userDAO.isEmailInThisWorkspace(email, workspaceId);
    }

    @Override
    public Optional<List<UserDTO>> getAllUsersDTO() {
        return userDAO.getAllUsersDTO();
    }

    @Override
    public Optional<UserDTO> getUserDTOById(Long id) {
        return userDAO.getUserDTOById(id);
    }

    @Override
    public Optional<UserDTO> getUserDTOByLogin(String login) {
        return userDAO.getUserDTOByLogin(login);
    }

    @Override
    public Optional<UserDTO> getUserDTOByName(String name) {
        return userDAO.getUserDTOByName(name);
    }

    @Override
    public Optional<UserDTO> getUserDTOByEmail(String email) {
        return userDAO.getUserDTOByEmail(email);
    }

    @Override
    public Optional<List<UserDTO>> getAllUsersDTOInThisChannel(Long id) {
        return userDAO.getAllUsersDTOInThisChannel(id);
    }

    @Override
    public User getEntityFromDTO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        // creating new User with simple fields copied from UserDTO
        User user = new User(userDTO);

        /* setting up 'password', roles, channels, workspaces, workspaceUserRoles
        ownedChannels, ownedWorkspaces, openingConversations, associatedConversations,
        messages
        * */
        Long id = userDTO.getId();
        if (id != null && user.getPassword() == null) {
            User existingUser = userDAO.getById(id);
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
                user.setRoles(existingUser.getRoles());
                user.setChannels(existingUser.getChannels());
                user.setWorkspaces(existingUser.getWorkspaces());
                user.setOwnedChannels(existingUser.getOwnedChannels());
                user.setOwnedWorkspaces(existingUser.getOwnedWorkspaces());
                user.setMessages(existingUser.getMessages());
                user.setWorkspaceUserRoles(existingUser.getWorkspaceUserRoles());
                user.setOpeningConversations(existingUser.getOpeningConversations());
                user.setAssociatedConversations(existingUser.getAssociatedConversations());
            }
        }

        // setting up 'starredMessages'
        List<Message> starredMessagesList = messageDAO.getMessagesByIds(userDTO.getStarredMessageIds(), false);
        user.setStarredMessages(new HashSet<>(starredMessagesList));

        // setting up 'unreadMessages'
        List<Message> unreadMessageList = messageDAO.getMessagesByIds(userDTO.getUnreadMessageIds(), false);
        user.setUnreadMessages(new HashSet<>(unreadMessageList));

        // setting up 'unreadDirectMessages'
        List<DirectMessage> unreadDirectMessageList = directMessageDAO.getMessagesByIds(userDTO.getUnreadDirectMessageIds(), false);
        user.setUnreadDirectMessages(new HashSet<>(unreadDirectMessageList));

        return user;
    }
}
