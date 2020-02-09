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
            return (List<Workspace>) entityManager.createNativeQuery("select * from workspace where owner_id = ?", Workspace.class)
                    .setParameter(1, user.getId())
                    .getResultList();
    }

    @Override
    public List<Workspace> getWorkspacesByUser(User user) {
        String query = "select ws.id, ws.name, ws.owner_id, ws.is_private, ws.created_date "
                + "from workspaces ws "
                + "right join workspace_user_role wur on ws.id = wur.workspace_id "
                + "where wur.user_id = :userid "
                + "group by ws.id";
        return entityManager.createNativeQuery(query, Workspace.class)
                .setParameter("userid", user.getId())
                .getResultList();
    }
}
