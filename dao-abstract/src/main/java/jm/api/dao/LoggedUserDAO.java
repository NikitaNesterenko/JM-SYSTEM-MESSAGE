package jm.api.dao;

import jm.analytic.LoggedUser;
import jm.model.User;

import java.util.List;

public interface LoggedUserDAO {

    List<LoggedUser> getAll();
    List<LoggedUser> getAllForWorkspace(Long workspaceId);
    List<LoggedUser> getAllForWorkspaceForLastMonth(Long workspaceId);
    void persist(LoggedUser loggedUser);
    void deleteById(Long id);
    LoggedUser merge(LoggedUser loggedUser);
    LoggedUser getById(Long id);
    LoggedUser getByUser(User user);
}
