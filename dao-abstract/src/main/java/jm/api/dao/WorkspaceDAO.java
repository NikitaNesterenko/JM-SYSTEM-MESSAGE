package jm.api.dao;

import jm.dto.WorkspaceDTO;
import jm.model.Workspace;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface WorkspaceDAO {

    List<Workspace> getAll();

    void persist(Workspace workspace);

    void deleteById(Long id);

    Workspace merge(Workspace workspace);

    Workspace getById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwnerId(Long ownerId);

    List<Workspace> getWorkspacesByUserId(Long userId);

    Optional<List<WorkspaceDTO>> getAllWorkspacesDTO();

    Optional<WorkspaceDTO> getWorkspaceDTOById(Long id);

    Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId);

    /**
     * Создает коллецию воркспейсов по Логину. Сравнивает
     *
     * ВНИМАНИЕ!!! на  15.03.2019 таблица workspaces_users содержит userId=1 на workspace, в которых нет юзера
     *
     * @param login
     * @return List<Workspace>
     */
    List<Workspace> getWorkspaceListByLogin(@NonNull String login);
}
