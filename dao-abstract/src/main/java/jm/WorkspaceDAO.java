package jm;

import java.util.List;

public interface WorkspaceDAO {

    List<Workspace> getAll();

    void persist(Workspace workspace);

    void deleteById(Long id);

    Workspace merge(Workspace workspace);

    Workspace getById(Long id);

    Workspace getWorkspaceByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);
}
