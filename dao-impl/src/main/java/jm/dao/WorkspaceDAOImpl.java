package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Repository
@Transactional
public class WorkspaceDAOImpl extends AbstractDao<Workspace> implements WorkspaceDAO {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceDAOImpl.class);

    @Override
    public Workspace getWorkspaceByName(String name) {
        try {
            return (Workspace) entityManager.createNativeQuery("select * from workspaces where name=?", Workspace.class)
                    .setParameter(1, name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Workspace> getWorkspacesByOwnerId(Long ownerId) {
        return (List<Workspace>) entityManager.createNativeQuery("select * from workspaces where owner_id=?", Workspace.class)
                .setParameter(1, ownerId)
                .getResultList();
    }

    @Override
    public List<Workspace> getWorkspacesByUserId(Long userId) {
//        TODO сделать одним запросом
        List workspacesIds = entityManager.createNativeQuery("" +
                "SELECT workspace_id " +
                "FROM workspace_user_role " +
                "WHERE user_id = :userId")
                .setParameter("userId", userId)
                .getResultList();

        return (List<Workspace>) entityManager.createNativeQuery("SELECT * FROM workspaces WHERE id IN (:ids)", Workspace.class)
                .setParameter("ids", workspacesIds).getResultList();
    }
}
