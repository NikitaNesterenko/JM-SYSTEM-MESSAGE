package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class WorkspaceDAOImpl implements WorkspaceDAO {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Workspace> gelAllChannels() {
        return entityManager.createNativeQuery("SELECT * FROM workspaces", Workspace.class).getResultList();
    }

    @Override
    public void createChannel(Workspace workspace) {
        entityManager.persist(workspace);
    }

    @Override
    public void deleteChannel(Workspace workspace) {
        Workspace searchedWorkspace = entityManager.find(Workspace.class, workspace.getId());
        if (searchedWorkspace != null) {
            entityManager.remove(searchedWorkspace);
        }
    }

    @Override
    public void updateChannel(Workspace workspace) {
        entityManager.merge(workspace);
        entityManager.flush();
    }

    @Override
    public Workspace getChannelById(Long id) {
        return entityManager.find(Workspace.class, id);
    }

    @Override
    public Workspace getChannelByName(String name) {
        return (Workspace) entityManager.createNativeQuery("select * from workspaces where name='" + name + "'", Workspace.class).getSingleResult();
    }

    @Override
    public List<Workspace> getWorkspacesByOwner(User user) {
        try {
            return entityManager.createNativeQuery("select * from workspaces where owner_id='" + user.getId() + "'", Workspace.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
