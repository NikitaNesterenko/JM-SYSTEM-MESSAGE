package jm;

import jm.model.LoggedUser;
import jm.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LoggedUserService {

    List<LoggedUser> getAllLoggedUsers();
    void createLoggedUser(LoggedUser loggedUser);
    void deleteLoggedUser(Long id);
    LoggedUser getLoggedUserById(Long id);
    LoggedUser getLoggedUserByUser(User user);
    LoggedUser getLoggedUserByName(String name);
    LoggedUser findOrCreateNewLoggedUser(Authentication authentication);
}
