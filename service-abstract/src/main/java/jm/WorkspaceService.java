package jm;

import jm.model.CreateWorkspaceToken;
import jm.dto.WorkspaceDTO;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;

public interface WorkspaceService {
    List<Workspace> getAllWorkspaces();

    void createWorkspace(Workspace workspace);

    void deleteWorkspace(Long id);

    void updateWorkspace(Workspace workspace);

    Workspace getWorkspaceById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwnerId(Long ownerId);

    List<Workspace> getWorkspacesByUserId(Long userId);

    void createWorkspaceByToken(CreateWorkspaceToken createWorkspaceToken);

    Optional<List<WorkspaceDTO>> getAllWorkspacesDTO();

    Optional<WorkspaceDTO> getWorkspaceDTOById(Long id);

    Optional<WorkspaceDTO> getWorkspaceDTOByName(String name);

    Optional<List<WorkspaceDTO>> getWorkspacesDTOByUserId(Long userId);

    Workspace getEntityFromDTO(WorkspaceDTO workspaceDto);

}
