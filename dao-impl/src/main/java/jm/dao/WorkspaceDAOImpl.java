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
            return (Workspace) entityManager.createNativeQuery("SELECT * FROM workspaces  WHERE name = :name", Workspace.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Workspace> getWorkspacesByOwner(User user) {
            return (List<Workspace>) entityManager.createNativeQuery("SELECT * FROM workspace WHERE owner_id = :id", Workspace.class)
                    .setParameter("id", user.getId())
                    .getResultList();
    }

    @Override
    public List<Workspace> getWorkspacesByUser(User user) {
        String query = "SELECT ws.id, ws.name, ws.owner_id, ws.is_private, ws.created_date "
                + "FROM workspaces ws "
                + "RIGHT JOIN workspace_user_role wur ON ws.id = wur.workspace_id "
                + "WHERE wur.user_id = :userid "
                + "GROUP BY ws.id";
        return entityManager.createNativeQuery(query, Workspace.class)
                .setParameter("userid", user.getId())
                .getResultList();
    }
}
