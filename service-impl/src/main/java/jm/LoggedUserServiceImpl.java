package jm;

import jm.api.dao.LoggedUserDAO;
import jm.api.dao.UserDAO;
import jm.model.LoggedUser;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoggedUserServiceImpl implements LoggedUserService {

    private LoggedUserDAO loggedUserDAO;
    private UserDAO userDAO;

    @Autowired
    public void setLoggedUserDAO(LoggedUserDAO loggedUserDAO) {
        this.loggedUserDAO = loggedUserDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<LoggedUser> getAllLoggedUsers() {
        return loggedUserDAO.getAll();
    }

    @Override
    public void createLoggedUser(LoggedUser loggedUser) {
        if (loggedUser.getId() != null) {
            loggedUserDAO.merge(loggedUser);
        } else {
            loggedUserDAO.persist(loggedUser);
        }
    }

    @Override
    public void deleteLoggedUser(Long id) {
        loggedUserDAO.deleteById(id);
    }

    @Override
    public LoggedUser getLoggedUserById(Long id) {
        return loggedUserDAO.getById(id);
    }

    @Override
    public LoggedUser getLoggedUserByUser(User user) {
        return loggedUserDAO.getByUser(user);
    }

    @Override
    public LoggedUser getLoggedUserByName(String name) {
        User user = userDAO.getUserByLogin(name);
        return getLoggedUserByUser(user);
    }

    @Override
    public LoggedUser findOrCreateNewLoggedUser(Authentication authentication) {
        String login = authentication.getName();
        LoggedUser loggedUser = this.getLoggedUserByName(login);
        if (loggedUser == null) {
            loggedUser = new LoggedUser();
            loggedUser.setUser(userDAO.getUserByLogin(login));
            loggedUser.setDateTime(LocalDateTime.now());
        }
        return loggedUser;
    }
}
