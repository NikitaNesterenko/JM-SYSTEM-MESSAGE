package jm;

import jm.model.App;

public interface AppsService {

    void createApp(App app);

    App getAppByWorkspaceIdAndAppName(Long id, String appName);
}
