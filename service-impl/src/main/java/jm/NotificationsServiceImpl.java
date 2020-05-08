package jm;

import jm.api.dao.NotificationsDAO;
import jm.model.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class NotificationsServiceImpl implements NotificationService {

    private NotificationsDAO dao;

    @Autowired
    public void setDao(NotificationsDAO dao) {
        this.dao = dao;
    }

    @Override
    public Notifications getNotification(Long userId, Long workspaceId) {
        return dao.getNotification(userId, workspaceId);
    }

    @Override
    @Transactional
    public void addNotification(Notifications notifications) {
        dao.addNotification(notifications);
    }

    @Override
    @Transactional
    public void updateNotification(Notifications notifications) {
        dao.updateNotification(notifications);
    }
}
