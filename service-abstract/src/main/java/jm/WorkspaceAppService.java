package jm;

import jm.model.WorkspaceApp.WorkspaceApp;

import java.util.List;

public interface WorkspaceAppService {

    List<WorkspaceApp> gelAllWorkspaceApp();

    void createWorkspaceApp(WorkspaceApp workspaceApp);

    void deleteWorkspaceApp(Long id);

    void updateWorkspaceApp(WorkspaceApp workspaceApp);

    WorkspaceApp getWorkspaceAppById(Long id);
}
