package jm;

import jm.model.User;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;

public interface WorkspaceService {
    List<Workspace> gelAllWorkspaces();

    void createWorkspace(Workspace workspace);

    void deleteWorkspace(Long id);

    void updateWorkspace(Workspace workspace);

    Workspace getWorkspaceById(Long id);

    Optional<Workspace> getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);

    List<Workspace> getWorkspacesByUser(User user);

}
