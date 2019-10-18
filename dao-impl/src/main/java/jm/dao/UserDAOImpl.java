package jm.dao;

import jm.api.dao.UserDAO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class UserDAOImpl extends AbstractDao<User> implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);


    @Override
    public User getUserByLogin(String login) {
        try {
            return (User) entityManager.createQuery("from User where login  = :login").setParameter("login", login).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void addRoleForUser(User user, String role) {

    }

    @Override
    public void updateUserRole(User user, String role) {

    }
}
