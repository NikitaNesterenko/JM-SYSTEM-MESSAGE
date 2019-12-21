package jm.dto;

import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.User;
import jm.model.message.Message;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setLogin(user.getLogin());
        userDTO.setEmail(user.getEmail());
        userDTO.setAvatarURL(user.getAvatarURL());
        userDTO.setTitle(user.getTitle());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setTimeZone(user.getTimeZone());
        userDTO.setOnline(user.getOnline());
        userDTO.setUserSkype(user.getUserSkype());

        Set<Long> starredMessageIds = user.getStarredMessages().stream().map(Message::getId).collect(Collectors.toSet());
        userDTO.setStarredMessageIds(starredMessageIds);

        Set<Long> directMessagesToUserIds = user.getDirectMessagesToUsers().stream().map(User::getId).collect(Collectors.toSet());
        userDTO.setDirectMessagesToUserIds(directMessagesToUserIds);

        return userDTO;
    }

    @Override
    public List<UserDTO> toDto(List<User> users) {
        return users == null ? null : users.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public User toEntity(UserDTO userDTO) {

        if (userDTO == null) {
            return null;
        }

        Long id = userDTO.getId();

        User user = new User();

        user.setId(id);
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setLogin(userDTO.getLogin());
        if (id != null && user.getPassword() == null) {
            User existingUser = userDAO.getById(id);
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }
        user.setEmail(userDTO.getEmail());
        user.setAvatarURL(userDTO.getAvatarURL());
        user.setTitle(userDTO.getTitle());
        user.setDisplayName(userDTO.getDisplayName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setTimeZone(userDTO.getTimeZone());
        user.setOnline(userDTO.getOnline());
        user.setUserSkype(userDTO.getUserSkype());

        Set<Long> starredMessageIds = userDTO.getStarredMessageIds();
        List<Message> starredMessagesList = messageDAO.getMessagesByIds(starredMessageIds);
        user.setStarredMessages(new HashSet<>(starredMessagesList));

        Set<Long> directMessagesToUserIds = userDTO.getDirectMessagesToUserIds();
        List<User> directMessagesToUserList = userDAO.getUsersByIds(directMessagesToUserIds);
        user.setDirectMessagesToUsers(new HashSet<>(directMessagesToUserList));

        return user;
    }

}
