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
            return (Workspace) entityManager.createNativeQuery("SELECT w.* FROM workspaces w WHERE  w.name = :name", Workspace.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Workspace> getWorkspacesByOwnerId(Long ownerId) {
        return (List<Workspace>) entityManager.createNativeQuery("SELECT w.* FROM workspaces w WHERE w.owner_id = :id", Workspace.class)
                .setParameter("id", ownerId)
                .getResultList();
    }

    @Override
    public List<Workspace> getWorkspacesByUserId(Long userId) {
        return (List<Workspace>) entityManager.createNativeQuery("SELECT w.* FROM workspaces_users wu JOIN workspaces w ON wu.workspace_id = w.id WHERE wu.user_id = :id", Workspace.class)
                .setParameter("id", userId)
                .getResultList();
    }
}
