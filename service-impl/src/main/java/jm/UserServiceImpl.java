package jm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Autowired
    public UserDAO setUserDAO(UserDAO userDAO) {
        return this.userDAO = userDAO;
    }

    @Autowired
    public RoleDAO setRoleDAO(RoleDAO roleDAO) {
        return this.roleDAO = roleDAO;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }


    public void createInitUser(User user, String role) {
        userDAO.createUser(user);
        roleDAO.addRoleForUser(user, role);
    }

    @Override
    public void createUser(User user) {
        userDAO.createUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    /*
        @Override
        public void updateUser(User user, String role) {
            userDAO.updateUser(user);
            roleDAO.updateUserRole(user, role);
        }
    */
    @Override
    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    @Override
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    @Override
    public User getUserByLogin(String login) {
        return userDAO.getUserByLogin(login);
    }

}
