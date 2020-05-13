package jm;

import jm.api.dao.AppsDAO;
import jm.model.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppsServiceImpl implements AppsService {
    private AppsDAO appsDAO;

    @Autowired
    public AppsServiceImpl(AppsDAO appsDAO) {
        this.appsDAO = appsDAO;
    }

    @Override
    public void createApp(App app) {
        appsDAO.persist(app);
    }

    @Override
    public void updateApp(App app) {
        appsDAO.merge(app);
    }

    @Override
    public void saveAppToken(Long workspaceId, String appName, String token) {
        App app = null;
        if (appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, appName).isPresent()) {
            app = appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, appName).get();
            app.setToken(token);
        }
    }

    @Override
    public String loadAppToken(Long workspaceId, String appName) {
        App app = null;
        if (appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, appName).isPresent()) {
            app = appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, appName).get();
        }
        return app.getToken();
    }

    @Override
    public App getAppByWorkspaceIdAndAppName(Long id, String appName) {
        App app = null;
        if (appsDAO.getAppByWorkspaceIdAndAppName(id, appName).isPresent()) {
            return appsDAO.getAppByWorkspaceIdAndAppName(id, appName).get();
        }
        return app;
    }
}
