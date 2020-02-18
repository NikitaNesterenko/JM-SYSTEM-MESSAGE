package jm;

import jm.api.dao.UserDAO;
import jm.dto.UserDTO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDAO userDAO;

    @Autowired
    public UserDAO setUserDAO(UserDAO userDAO) {
        return this.userDAO = userDAO;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    @Override
    public void createUser(User user) {
        userDAO.persist(user);
    }


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
        return userDAO.getUserByLogin(login);
    }

    @Override
    public User getUserByName(String name) {
        return userDAO.getUserByName(name);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    @Override
    public List<User> getAllUsersInThisChannel(Long id) {
        return userDAO.getAllUsersInThisChannel(id);
    }

    @Override
    public List<UserDTO> getAllUsersInWorkspace(Long id) {
        return userDAO.getUsersInWorkspace(id);
    }

}
