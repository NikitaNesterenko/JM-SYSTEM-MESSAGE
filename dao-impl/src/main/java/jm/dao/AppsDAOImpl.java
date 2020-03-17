package jm.dao;

import jm.api.dao.AppsDAO;
import jm.model.App;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class AppsDAOImpl extends AbstractDao<App> implements AppsDAO {

    @Override
    public App getAppByWorkspaceIdAndAppName(Long id, String appName) {
        try {
            return (App) entityManager.createNativeQuery("select * from apps where workspace_id = :workspace_id and app_name = :app_name", App.class)
                    .setParameter("workspace_id", id)
                    .setParameter("app_name", appName)
                    .getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }
}
