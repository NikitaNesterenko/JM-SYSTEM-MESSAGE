package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
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
    public List<Workspace> getWorkspacesByOwner(User user) {
        try {
            return entityManager.createQuery("from Workspace where owner_id = owner_id")
                    .setParameter("owner_id", user.getId()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<User> getAllUsersForWorkspace(Long id) {
        try {
            return entityManager.createQuery("select w.users from Workspace w where w.id = :ws_id")
                    .setParameter("ws_id", id).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
