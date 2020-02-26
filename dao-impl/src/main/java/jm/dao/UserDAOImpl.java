package jm.dao;

import jm.api.dao.UserDAO;
import jm.model.User;
import org.springframework.stereotype.Repository;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class UserDAOImpl extends AbstractDao<User> implements UserDAO {

    @Override
    public User getUserByLogin(String login) {
        try {
            return (User) entityManager.createQuery("from User where login  = :login").setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByName(String name) {
        try {
            return (User) entityManager.createQuery("from User where name  = :name").setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return (User) entityManager.createQuery("from User where email  = :email").setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    //TODO wtf
    @Override
    public void addRoleForUser(User user, String role) { }

    @Override
    public void updateUserRole(User user, String role) { }

    @Override
    public List<User> getAllUsersByChannel(Long channelID) {
            return entityManager.createNativeQuery("SELECT u.* FROM (users u JOIN channels_users cu ON u.id = cu.user_id) JOIN channels c ON c.id = cu.channel_id WHERE c.id = ?", User.class)
                    .setParameter(1, channelID)
                    .getResultList();
    }

    @Override
    public List<User> getAllUsersByWorkspace(Long workspaceID) {
        return entityManager.createNativeQuery("SELECT u.* FROM (users u JOIN workspaces_users wu ON u.id = wu.user_id) JOIN workspaces w ON w.id = wu.workspace_id WHERE w.id = ?", User.class)
                .setParameter(1, workspaceID)
                .getResultList();
    }

    @Override
    public List<User> getUsersByIDs(Set<Long> userIDs) {
        if (userIDs == null || userIDs.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select o from User o where o.id in :ids", User.class)
                .setParameter("ids", userIDs)
                .getResultList();
    }
}
