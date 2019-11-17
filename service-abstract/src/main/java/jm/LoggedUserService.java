package jm;

import jm.analytic.ChannelActivity;
import jm.analytic.LoggedUser;
import jm.analytic.MemberActivity;
import jm.analytic.MessageActivity;
import jm.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LoggedUserService {

    List<LoggedUser> getAllLoggedUsers();
    List<LoggedUser> getAllLoggedUsersForWorkspace(Long workspaceId);
    List<LoggedUser> getAllLoggedUsersForWorkspaceForLastMonth(Long workspaceId);
    void createLoggedUser(LoggedUser loggedUser);
    void deleteLoggedUser(Long id);
    LoggedUser getLoggedUserById(Long id);
    LoggedUser getLoggedUserByUser(User user);
    LoggedUser getLoggedUserByName(String name);
    LoggedUser findOrCreateNewLoggedUser(Authentication authentication);

    List<MemberActivity> getAllMemberActivityForWorkspace(Long workspaceId);
    List<MemberActivity> getAllMemberActivityForWorkspaceForLastMonth(Long workspaceId);

    List<ChannelActivity> getAllChannelsActivityForWorkspace(Long workspaceId);
    List<ChannelActivity> getAllChannelsActivityForWorkspaceForLastMonth(Long workspaceId);

    List<MessageActivity> getAllMessageActivityForWorkspace(Long workspaceId);
    List<MessageActivity> getAllMessageActivityForWorkspaceForLastMonth(Long workspaceId);
}
