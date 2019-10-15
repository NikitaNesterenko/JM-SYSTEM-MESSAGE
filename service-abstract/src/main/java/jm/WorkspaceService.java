package jm;

import java.util.List;

public interface WorkspaceService {

    List<Workspace> gelAllWorkspaces();

    void createWorkspace(Workspace workspace);

    void deleteWorkspace(Long id);

    void updateWorkspace(Workspace workspace);

    Workspace getWorkspaceById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);

}
