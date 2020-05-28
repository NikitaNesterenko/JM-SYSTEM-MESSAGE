package jm.api.dao;

import jm.model.App;

import java.util.Optional;

public interface AppsDAO {
    void persist(App app);

    App merge(App app);

    Optional<App> getAppByWorkspaceIdAndAppName(Long id, String appName);
}
