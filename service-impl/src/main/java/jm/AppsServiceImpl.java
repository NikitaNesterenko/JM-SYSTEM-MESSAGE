package jm;

import jm.api.dao.AppsDAO;
import jm.model.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppsServiceImpl implements AppsService {
    private static final Logger logger = LoggerFactory.getLogger(AppsServiceImpl.class);
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
    public App getAppByWorkspaceIdAndAppName(Long id, String appName) {
        return appsDAO.getAppByWorkspaceIdAndAppName(id, appName);
    }
}
