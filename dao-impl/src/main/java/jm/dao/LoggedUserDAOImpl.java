package jm.dao;

import jm.api.dao.LoggedUserDAO;
import jm.analytic.LoggedUser;
import jm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    @Override
    public List<LoggedUser> getAllForWorkspace(Long workspaceId) {
        try {
            return entityManager.createQuery("select lu from LoggedUser lu inner join lu.channels ch where ch.workspace.id = :ws_id", LoggedUser.class)
                    .setParameter("ws_id", workspaceId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<LoggedUser> getAllForWorkspaceForLastMonth(Long workspaceId) {
        try {
            return entityManager.createQuery("select lu from LoggedUser lu inner join lu.channels ch where lu.dateTime = :date_time and ch.workspace.id = :ws_id", LoggedUser.class)
                    .setParameter("date_time", LocalDateTime.now().minus(30, ChronoUnit.DAYS))
                    .setParameter("ws_id", workspaceId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
