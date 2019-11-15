package jm.api.dao;

import jm.model.LoggedUser;
import jm.model.User;

import java.util.List;

public interface LoggedUserDAO {

    List<LoggedUser> getAll();
    void persist(LoggedUser loggedUser);
    void deleteById(Long id);
    LoggedUser merge(LoggedUser loggedUser);
    LoggedUser getById(Long id);
    LoggedUser getByUser(User user);
}
