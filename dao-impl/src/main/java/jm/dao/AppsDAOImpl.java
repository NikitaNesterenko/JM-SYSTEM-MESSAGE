package jm.dao;

import jm.api.dao.AppsDAO;
import jm.model.App;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class AppsDAOImpl extends AbstractDao<App> implements AppsDAO {

    @Override
    public Optional<App> getAppByWorkspaceIdAndAppName(Long workspaceId, String appName) {
        if(haveEntityWithEntityNameAndId(workspaceId, "app_name", appName)) {
            return Optional.of((App) entityManager.createNativeQuery("select * from apps where workspace_id = :workspace_id and app_name = :app_name", App.class)
                    .setParameter("workspace_id", workspaceId)
                    .setParameter("app_name", appName)
                    .getSingleResult());
        }
        return Optional.empty();
    }
}
