package jm.api.dao;

import jm.model.User;
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

    List<Workspace> getWorkspacesByOwner(User user);

    List<Workspace> getWorkspacesByUser(User user);
}
