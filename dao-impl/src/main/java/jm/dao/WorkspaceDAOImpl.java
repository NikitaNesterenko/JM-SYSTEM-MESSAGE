package jm.dao;

import jm.api.dao.WorkspaceDAO;
import jm.dto.WorkspaceDTO;
import jm.model.Workspace;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class WorkspaceDAOImpl extends AbstractDao<Workspace> implements WorkspaceDAO {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceDAOImpl.class);

    @Override
    public Optional<Workspace> getWorkspaceByName(String name) {
        if (twoParametersMethodToSearchEntity("name", name)) {
            return Optional.of((Workspace) entityManager.createNativeQuery("select * from workspaces where name=?", Workspace.class)
                    .setParameter(1, name)
                    .getSingleResult());
        }
        return Optional.empty();
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
        List<WorkspaceDTO> workspaceDTOList = null;
        workspaceDTOList = (List<WorkspaceDTO>) entityManager
                .createNativeQuery("SELECT " +
                        "ws.id AS \"id\", " +
                        "ws.name AS \"name\", " +
                        "ws.created_date AS \"createdDate\", " +
                        "ws.is_private AS \"isPrivate\", " +
                        "owner_id AS \"ownerId\" " +
                        "FROM workspaces ws")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(WorkspaceDTO.class))
                .getResultList();
        workspaceDTOList.forEach(this::setWorkspaceDTOCollections);
        return Optional.ofNullable(workspaceDTOList);
    }

    @Override
    public Optional<WorkspaceDTO> getWorkspaceDTOById(Long id) {
        WorkspaceDTO workspaceDTO = null;
        if (haveEntityWithThisId(id)) {
            workspaceDTO = (WorkspaceDTO) entityManager
                    .createNativeQuery("SELECT " +
                            "ws.id as \"id\", " +
                            "ws.name as \"name\", " +
                            "ws.created_date as \"createdDate\", " +
                            "ws.is_private as \"isPrivate\", " +
                            "owner_id as \"ownerId\" " +
                            "from workspaces ws where ws.id=:id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(WorkspaceDTO.class))
                    .getResultList().get(0);
            setWorkspaceDTOCollections(workspaceDTO);

        }
        return Optional.ofNullable(workspaceDTO);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Optional<WorkspaceDTO> getWorkspaceDTOByName(String name) {
        WorkspaceDTO workspaceDTO = null;
        if (twoParametersMethodToSearchEntity("name", name)) {
            workspaceDTO = (WorkspaceDTO) entityManager.createNativeQuery("SELECT " +
                    "ws.id as \"id\", " +
                    "ws.name as \"name\", " +
                    "ws.created_date as \"createdDate\", " +
                    "ws.is_private as \"isPrivate\", " +
                    "owner_id as \"ownerId\" " +
                    "from workspaces ws where ws.name=:name")
                    .setParameter("name", name)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(WorkspaceDTO.class))
                    .getResultList().get(0);
            setWorkspaceDTOCollections(workspaceDTO);
        }
        return Optional.ofNullable(workspaceDTO);
    }

    @Override
    public Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId) {
        List<WorkspaceDTO> workspaceDTOList = null;
        try {
            workspaceDTOList = (List<WorkspaceDTO>) entityManager
                    .createNativeQuery("SELECT " +
                            "ws.id AS \"id\", " +
                            "ws.name AS \"name\", " +
                            "ws.created_date AS \"createdDate\", " +
                            "ws.is_private AS \"isPrivate\", " +
                            "owner_id AS \"ownerId\" " +
                            "FROM workspaces_users wu " +
                            "JOIN workspaces ws ON ws.id = wu.workspace_id " +
                            "WHERE wu.user_id=:userId")
                    .setParameter("userId", userId)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(WorkspaceDTO.class))
                    .getResultList();
            workspaceDTOList.forEach(this::setWorkspaceDTOCollections);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(workspaceDTOList);
    }


    private Set<Long> getBotIds(Long workspaceId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT wb.bot_id " +
                "FROM workspaces_bots wb WHERE wb.workspace_id =:id")
                .setParameter("id", workspaceId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getChannelIds(Long workspaceId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT c.id " +
                "FROM channels c WHERE c.workspace_id =:id")
                .setParameter("id", workspaceId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getUserIds(Long workspaceId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT wu.user_id " +
                "FROM workspaces_users wu WHERE wu.workspace_id =:id")
                .setParameter("id", workspaceId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getAppIds(Long workspaceId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT a.id " +
                "FROM apps a WHERE a.workspace_id =:id")
                .setParameter("id", workspaceId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private void setWorkspaceDTOCollections(WorkspaceDTO workspaceDTO) {
        workspaceDTO.setAppIds(getAppIds(workspaceDTO.getId()));
        workspaceDTO.setBotIds(getBotIds(workspaceDTO.getId()));
        workspaceDTO.setChannelIds(getChannelIds(workspaceDTO.getId()));
        workspaceDTO.setUserIds(getUserIds(workspaceDTO.getId()));
    }
}
