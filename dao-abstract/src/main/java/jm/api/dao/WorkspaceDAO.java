package jm.api.dao;

import jm.model.User;
import jm.model.Workspace;

import java.util.List;

public interface WorkspaceDAO {

    List<Workspace> gelAllChannels();

    void createChannel(Workspace workspace);

    void deleteChannel(Workspace workspace);

    void updateChannel(Workspace workspace);

    Workspace getChannelById(int id);

    Workspace getChannelByName(String name);

    List<Workspace> getWorkspacesByOwner(User user);
}
