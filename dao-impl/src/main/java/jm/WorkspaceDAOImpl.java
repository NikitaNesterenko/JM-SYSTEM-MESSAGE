package jm;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class WorkspaceDAOImpl implements WorkspaceDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Workspace> gelAllWorkspaces() {
        try {
            return (List<Workspace>) entityManager.createNativeQuery("SELECT * from workspaces", Workspace.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void createWorkspace(Workspace workspace) {
        entityManager.persist(workspace);
    }

    @Override
    public void deleteWorkspace(Workspace workspace) {
        entityManager.remove(workspace);
    }

    @Override
    public void updateWorkspace(Workspace workspace) {
        entityManager.merge(workspace);
        entityManager.flush();
    }

    @Override
    public Workspace getWorkspaceById(int id) {
        try {
            return (Workspace) entityManager.createNativeQuery("select * from workspaces where id=?", Workspace.class)
                    .setParameter(1, id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

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
    @SuppressWarnings("unchecked")
    public List<Workspace> getWorkspacesByOwner(User user) {
        try {
            return (List<Workspace>) entityManager.createNativeQuery("select * from workspaces where owner_id=?", Workspace.class)
                    .setParameter(1, user.getId());
        } catch (NoResultException e) {
            return null;
        }
    }
}
