package jm.dao;

import jm.api.dao.UserDAO;
import jm.dto.UserDTO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

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

    @Override
    public void addRoleForUser(User user, String role) { }

    @Override
    public void updateUserRole(User user, String role) { }

    @Override
    public List<User> getAllUsersInThisChannel(Long id) {
            TypedQuery<User> query = (TypedQuery<User>) entityManager.createNativeQuery("SELECT u.* FROM (users u JOIN channels_users cu  ON u.id = cu.user_id) JOIN channels c ON c.id = cu.channel_id WHERE c.id = ?", User.class)
                    .setParameter(1, id);
            List<User> userList = query.getResultList();
            for (User user : userList) {
                System.out.println(user);
            }
            return userList;
    }

//    @Override
//    public List<User> getAllUsersInThisChannel(Long id) {
//        return entityManager.createQuery("from User u join Channel ch where ch.user.id = u.id").getResultList();
//            TypedQuery<User> query = (TypedQuery<User>) entityManager.createNativeQuery("SELECT u.* FROM (users u JOIN channels_users cu  ON u.id = cu.user_id) JOIN channels c ON c.id = cu.channel_id WHERE c.id = ?", User.class)
//                    .setParameter(1, id);
//            List<User> userList = query.getResultList();
//            for (User user : userList) {
//                System.out.println(user);
//            }
//            return userList;
//    }

    @Override
    public List<UserDTO> getUsersInWorkspace(Long id) {
        String query = "SELECT u.id, u.name, u.last_name, u.avatar_url, u.display_name " +
                "FROM workspace_user_role wur " +
                "INNER JOIN users u ON wur.user_id = u.id " +
                "INNER JOIN workspaces ws ON wur.workspace_id = ws.id " +
                "WHERE (ws.id = :workspace) " +
                "GROUP BY u.id";

        return entityManager.createNativeQuery(query, "UserDTOMapping")
                .setParameter("workspace", id)
                .getResultList();
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select o from User o where o.id in :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }

}
