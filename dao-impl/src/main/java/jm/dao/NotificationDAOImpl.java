package jm.dao;

import jm.api.dao.NotificationsDAO;
import jm.model.Notifications;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@Repository
public class NotificationDAOImpl extends AbstractDao<Notifications> implements NotificationsDAO {

    @Override
    public Notifications getNotification(Long userId, Long workspaceId) {
        try {
            return entityManager.createQuery("from Notifications where user_id = :user_id and" +
                    " workspace_id = :workspace_id", Notifications.class)
                    .setParameter("user_id", userId)
                    .setParameter("workspace_id", workspaceId).getSingleResult();
        } catch (NonUniqueResultException | NoResultException e) {
            return new Notifications();
        }
    }

    @Override
    public void addNotification(Notifications notifications) {
        if (getNotification(notifications.getUserId(), notifications.getWorkspaceId()).getId() == null) {
            entityManager.persist(notifications);
        }
    }

    @Override
    public void updateNotification(Notifications notifications) {
        entityManager.merge(notifications);
    }
}
