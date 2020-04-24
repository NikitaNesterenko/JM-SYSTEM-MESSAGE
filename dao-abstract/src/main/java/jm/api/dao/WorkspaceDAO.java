package jm.api.dao;

import jm.dto.WorkspaceDTO;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;

public interface WorkspaceDAO {

    List<Workspace> getAll();

    void persist(Workspace workspace);

    void deleteById(Long id);

    Workspace merge(Workspace workspace);

    Workspace getById(Long id);

    Optional<Workspace> getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwnerId(Long ownerId);

    List<Workspace> getWorkspacesByUserId(Long userId);

    Optional<List<WorkspaceDTO>> getAllWorkspacesDTO();

    Optional<WorkspaceDTO> getWorkspaceDTOById(Long id);

    Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId);
}
