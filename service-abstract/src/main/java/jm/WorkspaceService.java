package jm;

import jm.model.CreateWorkspaceToken;
import jm.model.Workspace;

import java.util.List;

public interface WorkspaceService {
    List<Workspace> gelAllWorkspaces();

    void createWorkspace(Workspace workspace);

    void deleteWorkspace(Long id);

    void updateWorkspace(Workspace workspace);

    Workspace getWorkspaceById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwnerId(Long ownerId);

    List<Workspace> getWorkspacesByUserId(Long userId);

    void createWorkspaceByToken(CreateWorkspaceToken createWorkspaceToken);

}
