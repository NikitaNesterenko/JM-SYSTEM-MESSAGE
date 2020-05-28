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
    public String searchInBaseToDeleteTryCatchInMethod(Long userId, Long workspaceId) {
        return dao.searchInBaseToDeleteTryCatchInMethod(userId, workspaceId);
    }

    @Override
    public Notifications getNotification(Long userId, Long workspaceId) {
        String exist = searchInBaseToDeleteTryCatchInMethod(userId, workspaceId);
        if (exist.equals("1")) {
            return dao.getNotification(userId, workspaceId);
        }
        return new Notifications();
    }

    @Override
    @Transactional
    public void addNotification(Notifications notifications) {
        String exist = searchInBaseToDeleteTryCatchInMethod(notifications.getUserId(), notifications.getWorkspaceId());
        if (exist.equals("0")) {
            dao.addNotification(notifications);
        }
    }

    @Override
    @Transactional
    public void updateNotification(Notifications notifications) {
        dao.updateNotification(notifications);
    }
}
