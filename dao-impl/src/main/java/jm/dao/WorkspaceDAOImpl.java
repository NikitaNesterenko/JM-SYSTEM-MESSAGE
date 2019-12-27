package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class WorkspaceDAOImpl extends AbstractDao<Workspace> implements WorkspaceDAO {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceDAOImpl.class);

    @Override
    public Optional<Workspace> getWorkspaceByName(String name) {
            return Optional.ofNullable((Workspace) entityManager.createNativeQuery("SELECT * FROM workspaces WHERE name=?", Workspace.class)
                    .setParameter(1, name)
                    .getSingleResult());
    }

    @Override
    public List<Workspace> getWorkspacesByOwner(User user) {
            return (List<Workspace>) entityManager.createNativeQuery("SELECT * FROM workspaces WHERE owner_id=?", Workspace.class)
                    .setParameter(1, user.getId())
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
