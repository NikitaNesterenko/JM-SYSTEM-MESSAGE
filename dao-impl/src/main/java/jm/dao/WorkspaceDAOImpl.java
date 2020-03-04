package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.dto.WorkspaceDTO;
import jm.model.Workspace;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
        return (List<Workspace>) entityManager.createNativeQuery("SELECT w.* FROM workspaces_users wu JOIN workspaces w ON wu.workspace_id = w.id WHERE wu.user_id =?", Workspace.class)
                .setParameter(1, userId).getResultList();
    }

    @Override
    public Optional<List<WorkspaceDTO>> getAllWorkspacesDTO() {
        List<WorkspaceDTO> test = null;
        try {
            test = (List<WorkspaceDTO>) entityManager
                    .createQuery("select new jm.dto.WorkspaceDTO(ws) from Workspace ws", WorkspaceDTO.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(test);
    }

/*    @Override
    public Optional<WorkspaceDTO> getWorkspaceDTOById(Long id) {
        WorkspaceDTO test = null;
        try {
            test = (WorkspaceDTO) entityManager.createQuery("select new jm.dto.WorkspaceDTO(ws) " +
                    "from Workspace ws where ws.id=:id", WorkspaceDTO.class).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(test);
    }    */
    /* TODO доделать Tuple

     */
    @Override
    public Optional<WorkspaceDTO> getWorkspaceDTOById(Long id) {
        WorkspaceDTO test = null;
        Tuple ttt;
        try {
            List<Long> botIds = entityManager.createNativeQuery("SELECT wb.bot_id " +
                    "FROM workspaces_bots wb WHERE wb.workspace_id =:id")
                    .setParameter("id", id)
                    .getResultList();

            List<Long> channelIds = entityManager.createNativeQuery("SELECT c.id " +
                    "FROM channels c WHERE c.workspace_id =:id")
                    .setParameter("id", id)
                    .getResultList();

            List<Long> userIds = entityManager.createNativeQuery("SELECT wu.user_id " +
                    "FROM workspaces_users wu WHERE wu.workspace_id =:id")
                    .setParameter("id", id)
                    .getResultList();

            List<Long> appId = entityManager.createNativeQuery("SELECT a.id " +
                    "FROM apps a WHERE a.workspace_id =:id")
                    .setParameter("id", id)
                    .getResultList();

            ttt = (Tuple) entityManager.createNativeQuery("select ws.id, ws.name, ws.created_date, " +
                    "ws.google_client_id, ws.google_client_secret, ws.is_private, owner_id" +
                    " from workspaces ws " +
                    "where ws.id=:id", Tuple.class).setParameter("id", id).getResultList().get(0);
            test = new WorkspaceDTO.Builder().setId(((BigInteger) ttt.get("id")).longValue()).setName((String) ttt.get("name"))
                    .setCreatedDate((LocalDateTime) ttt.get("created_date")).setOwnerId((Long) ttt.get("owner_id"))
                    .setPrivate((Boolean) ttt.get("is_private")).setUserIds(new HashSet<>(userIds))
                    .setChannelIds(new HashSet<>(channelIds)).setBotsIds(new HashSet<>(botIds)).setAppsIds(new HashSet<>(appId)).build();
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(test);
    }

    @Override
    public Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId) {
        return Optional.empty();
    }
}
