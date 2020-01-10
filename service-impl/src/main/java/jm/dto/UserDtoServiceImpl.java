package jm.dto;

import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.User;
import jm.model.Message;
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

        if (user == null) { return null; }

        // creating new UserDTO with simple fields copied from User
        UserDTO userDTO = new UserDTO(user);

        // setting up 'starredMessageIds'
        if (user.getStarredMessages() != null) {
            Set<Long> starredMessageIds = user.getStarredMessages().stream().map(Message::getId).collect(Collectors.toSet());
            userDTO.setStarredMessageIds(starredMessageIds);
        }
        return userDTO;
    }

    @Override
    @Transactional
    public User toEntity(UserDTO userDTO) {

        if (userDTO == null) { return null; }

        // creating new User with simple fields copied from UserDTO
        User user = new User(userDTO);

        // setting up 'password'
        Long id = userDTO.getId();
        if (id != null && user.getPassword() == null) {
            User existingUser = userDAO.getById(id);
            if (existingUser != null) { user.setPassword(existingUser.getPassword()); }
        }

        // setting up 'starredMessages'
        //user.setStarredMessages(userDTO.getStarredMessages());
        List<Message> starredMessagesList = messageDAO.getMessagesByIds(userDTO.getStarredMessageIds());
        user.setStarredMessages(new HashSet<>(starredMessagesList));
        return user;
    }

}
