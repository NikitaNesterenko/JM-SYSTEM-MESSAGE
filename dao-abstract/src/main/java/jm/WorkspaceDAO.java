package jm;

import java.util.List;

public interface WorkspaceDAO {

    List<Workspace> gelAllWorkspaces();

    void createWorkspace(Workspace workspace);

    void deleteWorkspace(Workspace workspace);

    void updateWorkspace(Workspace workspace);

    Workspace getWorkspaceById(int id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);
}
