package jm;

import jm.model.Notifications;

public interface NotificationService {

        String searchInBaseToDeleteTryCatchInMethod(Long userId, Long workspaceId);

        Notifications getNotification(Long userId, Long workspaceId);

        void addNotification(Notifications notifications);

        void updateNotification(Notifications notifications);
}
