package jm.api.dao;

import jm.model.App;

public interface AppsDAO {

    void persist(App app);

    App getAppByWorkspaceIdAndAppName(Long id, String appName);
}
