package jm.api.dao;

import jm.model.Workspace;

import java.util.List;

public interface WorkspaceDAO {

    List<Workspace> getAll();

    void persist(Workspace workspace);

    void deleteById(Long id);

    Workspace merge(Workspace workspace);

    Workspace getById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwnerId(Long ownerId);

    List<Workspace> getWorkspacesByUserId(Long userId);
}
