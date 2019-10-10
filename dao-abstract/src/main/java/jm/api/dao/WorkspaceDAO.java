package jm.api.dao;

import jm.model.User;
import jm.model.Workspace;

import java.util.List;

public interface WorkspaceDAO {

    List<Workspace> getAll();

    void persist(Workspace workspace);

    void deleteById(Long id);

    Workspace merge(Workspace workspace);

    Workspace getById(Long id);

    Workspace getChannelByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);
}
