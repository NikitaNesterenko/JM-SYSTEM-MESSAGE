package jm;

import jm.model.App;

public interface AppsService {
    void createApp(App app);

    void updateApp(App app);

    void saveAppToken(Long workspaceId, String appName, String token);

    String loadAppToken(Long workspaceId, String appName);

    App getAppByWorkspaceIdAndAppName(Long id, String appName);
}
