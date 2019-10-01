package jm;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class WorkspaceDAOImpl implements WorkspaceDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Workspace> gelAllChannels() {
        return entityManager.createQuery("from Workspace").getResultList();
    }

    @Override
    public void createChannel(Workspace workspace) {
        entityManager.persist(workspace);
    }

    @Override
    public void deleteChannel(Workspace workspace) {
        entityManager.remove(entityManager.contains(workspace)? workspace : entityManager.merge(workspace));
    }

    @Override
    public void updateChannel(Workspace workspace) {
        entityManager.merge(workspace);
        entityManager.flush();
    }

    @Override
    public Workspace getChannelById(int id) {
        return entityManager.find(Workspace.class, id);
    }

    @Override
    public Workspace getChannelByName(String name) {
        return (Workspace) entityManager.createQuery("from Workspace where name  = :name")
                .setParameter("name", name)
                .getSingleResult();
    }
}
