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
        return entityManager.createQuery("from Workspace").getResultList();
    }

    @Override
    public void createWorkspace(Workspace workspace) {
        User user;
        user = workspace.getUser();
        workspace.setUser(user);
        entityManager.persist(workspace);
    }

    @Override
    public void deleteWorkspace(Workspace workspace) {
        entityManager.remove(entityManager.contains(workspace) ? workspace : entityManager.merge(workspace));
    }

    @Override
    public void updateWorkspace(Workspace workspace) {
        entityManager.merge(workspace);
        entityManager.flush();
    }

    @Override
    public Workspace getWorkspaceById(int id) {
//        Integer intid = id;
        return entityManager.find(Workspace.class, id);
    }

    @Override
    public Workspace getWorkspaceByName(String name) {
        return (Workspace) entityManager.createQuery("from Workspace where name  = :name")
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Workspace> getWorkspacesByOwner(User user) {
        try {
            return entityManager.createQuery("from Workspace where owner_id = owner_id").setParameter("owner_id", user.getId()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
