package jm.dto;

import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.User;
import jm.model.message.Message;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDtoServiceImpl implements UserDtoService {

    private final UserDAO userDAO;
    private final MessageDAO messageDAO;

    public UserDtoServiceImpl(UserDAO userDAO, MessageDAO messageDAO) {
        this.userDAO = userDAO;
        this.messageDAO = messageDAO;
    }

    @Override
    public UserDTO toDto(User user) {

        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO(user);

        Set<Long> starredMessageIds = user.getStarredMessages().stream().map(Message::getId).collect(Collectors.toSet());
        userDTO.setStarredMessageIds(starredMessageIds);

        Set<Long> directMessagesToUserIds = user.getDirectMessagesToUsers().stream().map(User::getId).collect(Collectors.toSet());
        userDTO.setDirectMessagesToUserIds(directMessagesToUserIds);

        return userDTO;
    }

    @Override
    @Transactional
    public User toEntity(UserDTO userDTO) {

        if (userDTO == null) {
            return null;
        }

        User user = new User(userDTO);

        Long id = userDTO.getId();
        if (id != null && user.getPassword() == null) {
            User existingUser = userDAO.getById(id);
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }

        // starredMessages
        Set<Long> starredMessageIds = userDTO.getStarredMessageIds();
        List<Message> starredMessagesList = messageDAO.getMessagesByIds(starredMessageIds);
        user.setStarredMessages(new HashSet<>(starredMessagesList));

        // directMessagesToUsers
        Set<Long> directMessagesToUserIds = userDTO.getDirectMessagesToUserIds();
        List<User> directMessagesToUserList = userDAO.getUsersByIds(directMessagesToUserIds);
        user.setDirectMessagesToUsers(new HashSet<>(directMessagesToUserList));

        return user;
    }

}
