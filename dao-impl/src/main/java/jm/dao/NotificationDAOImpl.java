package jm.dao;

import jm.api.dao.NotificationsDAO;
import jm.model.Notifications;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDAOImpl extends AbstractDao<Notifications> implements NotificationsDAO {

    public String searchInBaseToDeleteTryCatchInMethod(Long userId, Long workspaceId) {
        return entityManager.createNativeQuery("SELECT EXISTS(SELECT id FROM notifications WHERE " +
                "user_id = :user_id  and workspace_id = :workspace_id )")
                .setParameter("user_id", userId)
                .setParameter("workspace_id", workspaceId).getSingleResult().toString();
    }

    @Override
    public Notifications getNotification(Long userId, Long workspaceId) {
        return entityManager.createQuery("from Notifications where user_id = :user_id  and" +
                " workspace_id = :workspace_id", Notifications.class)
                .setParameter("user_id", userId)
                .setParameter("workspace_id", workspaceId).getSingleResult();
    }

    @Override
    public void addNotification(Notifications notifications) {
        entityManager.persist(notifications);
    }

    @Override
    public void updateNotification(Notifications notifications) {
        entityManager.merge(notifications);
    }
}
