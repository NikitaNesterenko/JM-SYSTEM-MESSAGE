package jm.api.dao;

import jm.model.WorkspaceApp.WorkspaceApp;

import java.util.List;

public interface WorkspaceAppDAO {

    List<WorkspaceApp> getAll();

    void persist(WorkspaceApp workspace);

    void deleteById(Long id);

    WorkspaceApp merge(WorkspaceApp workspace);

    WorkspaceApp getById(Long id);

}
