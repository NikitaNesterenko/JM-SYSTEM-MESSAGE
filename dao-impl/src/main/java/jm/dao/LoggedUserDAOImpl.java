package jm.dao;

import jm.api.dao.LoggedUserDAO;
import jm.model.LoggedUser;
import jm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class LoggedUserDAOImpl extends AbstractDao<LoggedUser> implements LoggedUserDAO {
    @Override
    public LoggedUser getByUser(User user) {
        try {
            return entityManager.createQuery("from LoggedUser lu where lu.user = :user", LoggedUser.class)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
