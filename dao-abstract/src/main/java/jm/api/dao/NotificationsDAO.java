package jm.api.dao;

import jm.model.Notifications;

public interface NotificationsDAO {

    String searchInBaseToDeleteTryCatchInMethod(Long userId, Long workspaceId);

    Notifications getNotification(Long userId, Long workspaceId);

    void addNotification(Notifications notifications);

    void updateNotification(Notifications notifications);
}
