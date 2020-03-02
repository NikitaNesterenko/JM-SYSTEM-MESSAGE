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
import java.util.Arrays;
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
        try {
            TypedQuery<Tuple> ttt = (TypedQuery<Tuple>) entityManager.createNativeQuery("select ws.id, ws.name, ws.bots " +
                    "from workspaces ws " +
                    "where ws.id=:id", Tuple.class).setParameter("id", id);
            test = (WorkspaceDTO) entityManager.createNativeQuery("select ws.id as \"id\", ws.name as \"name\" " +
                    "from workspaces ws where ws.id=:id").setParameter("id", id)
                    .unwrap(org.hibernate.query.NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(WorkspaceDTO.class))
                    .getSingleResult();
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
