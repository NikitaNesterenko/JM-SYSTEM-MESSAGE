package jm;

import jm.model.Channel;
import jm.model.User;

import java.util.List;

public interface AnalyticService {

    Integer getMessagesCountForWorkspace(Long workspaceId);

    List<Channel> getAllChannelsForWorkspace(Long workspaceId, Boolean lastMonth);

    List<User> getUsersForWorkspaceByIdForLastMonth(Long workspaceId);

    List<User> getUsersForWorkspaceById(Long workspaceId);

}
