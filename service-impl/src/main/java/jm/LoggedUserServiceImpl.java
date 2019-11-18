package jm;

import jm.analytic.ChannelActivity;
import jm.analytic.LoggedUser;
import jm.analytic.MemberActivity;
import jm.analytic.MessageActivity;
import jm.api.dao.LoggedUserDAO;
import jm.api.dao.UserDAO;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoggedUserServiceImpl implements LoggedUserService {

    private LoggedUserDAO loggedUserDAO;
    private UserDAO userDAO;

    @Autowired
    public void setLoggedUserDAO(LoggedUserDAO loggedUserDAO) {
        this.loggedUserDAO = loggedUserDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<LoggedUser> getAllLoggedUsers() {
        return loggedUserDAO.getAll();
    }

    @Override
    public void createLoggedUser(LoggedUser loggedUser) {
        if (loggedUser.getId() != null) {
            loggedUserDAO.merge(loggedUser);
        } else {
            loggedUserDAO.persist(loggedUser);
        }
    }

    @Override
    public void deleteLoggedUser(Long id) {
        loggedUserDAO.deleteById(id);
    }

    @Override
    public LoggedUser getLoggedUserById(Long id) {
        return loggedUserDAO.getById(id);
    }

    @Override
    public LoggedUser getLoggedUserByUser(User user) {
        return loggedUserDAO.getByUser(user);
    }

    @Override
    public LoggedUser getLoggedUserByName(String name) {
        User user = userDAO.getUserByLogin(name);
        return getLoggedUserByUser(user);
    }

    @Override
    public List<LoggedUser> getAllLoggedUsersForWorkspace(Long workspaceId) {
        return loggedUserDAO.getAllForWorkspace(workspaceId);
    }

    @Override
    public List<LoggedUser> getAllLoggedUsersForWorkspaceForLastMonth(Long workspaceId) {
        return loggedUserDAO.getAllForWorkspaceForLastMonth(workspaceId);
    }

    @Override
    public List<MemberActivity> getAllMemberActivityForWorkspace(Long workspaceId) {
        return new ArrayList<>(this.getMemberActivityMap(workspaceId, false).values());
    }

    @Override
    public List<MemberActivity> getAllMemberActivityForWorkspaceForLastMonth(Long workspaceId) {
        return new ArrayList<>(this.getMemberActivityMap(workspaceId, true).values());
    }

    @Override
    public List<ChannelActivity> getAllChannelsActivityForWorkspace(Long workspaceId) {
        return new ArrayList<>(this.getChannelActivityMap(workspaceId, false).values());
    }

    @Override
    public List<ChannelActivity> getAllChannelsActivityForWorkspaceForLastMonth(Long workspaceId) {
        return new ArrayList<>(this.getChannelActivityMap(workspaceId, true).values());
    }

    @Override
    public List<MessageActivity> getAllMessageActivityForWorkspace(Long workspaceId) {
        return new ArrayList<>(this.getMessageActivityMap(workspaceId, false).values());
    }

    @Override
    public List<MessageActivity> getAllMessageActivityForWorkspaceForLastMonth(Long workspaceId) {
        return new ArrayList<>(this.getMessageActivityMap(workspaceId, true).values());
    }

    @Override
    public LoggedUser findOrCreateNewLoggedUser(Authentication authentication) {
        String login = authentication.getName();
        LoggedUser loggedUser = this.getLoggedUserByName(login);
        if (loggedUser == null
                // если день создания отличается от текущего
                || loggedUser.getDateTime().getDayOfYear() != LocalDateTime.now().getDayOfYear()) {
            loggedUser = new LoggedUser();
            loggedUser.setUser(userDAO.getUserByLogin(login));
            loggedUser.setDateTime(LocalDateTime.now());
        }
        return loggedUser;
    }

    private Map<LocalDate, MemberActivity> getMemberActivityMap(Long workspaceId, boolean lastMonth) {

        List<LoggedUser> loggedUsers = null;
        if (lastMonth) {
            loggedUsers = this.getAllLoggedUsersForWorkspaceForLastMonth(workspaceId);
        } else {
            loggedUsers = this.getAllLoggedUsersForWorkspace(workspaceId);
        }

        Map<LocalDate, MemberActivity> activityMap = new HashMap<>();
        for (LoggedUser loggedUser : loggedUsers) {
            MemberActivity memberActivity = null;
            LocalDate loggedUserDate = loggedUser.getDateTime().toLocalDate();
            if (activityMap.containsKey(loggedUserDate)) {
                memberActivity = activityMap.get(loggedUserDate);
            } else {
                memberActivity = new MemberActivity();
                memberActivity.setDate(loggedUserDate);
            }

            memberActivity.setVisits(memberActivity.getVisits() + 1);
            if (loggedUser.getMessages().size() != 0) {
                memberActivity.setVisitsWithPosts(memberActivity.getVisitsWithPosts() + 1);
            }
            activityMap.put(loggedUserDate, memberActivity);
        }
        return activityMap;
    }

    private Map<LocalDate, ChannelActivity> getChannelActivityMap(Long workspaceId, boolean lastMonth) {

        List<LoggedUser> loggedUsers = null;
        if (lastMonth) {
            loggedUsers = this.getAllLoggedUsersForWorkspaceForLastMonth(workspaceId);
        } else {
            loggedUsers = this.getAllLoggedUsersForWorkspace(workspaceId);
        }

        Map<LocalDate, ChannelActivity> activityMap = new HashMap<>();
        for(LoggedUser loggedUser : loggedUsers) {
            ChannelActivity channelActivity;
            LocalDate loggedUserDate = loggedUser.getDateTime().toLocalDate();
            if (activityMap.containsKey(loggedUserDate)) {
                channelActivity = activityMap.get(loggedUserDate);
            } else {
                channelActivity = new ChannelActivity();
                channelActivity.setDate(loggedUserDate);
            }

            for (Channel channel : loggedUser.getChannels()) {
                if (channel.getIsPrivate()) {
                    channelActivity.setPrivateChannels(channelActivity.getPrivateChannels() + 1);
                } else {
                    channelActivity.setPublicChannels(channelActivity.getPublicChannels() + 1);
                }
            }

            for (Message message : loggedUser.getMessages()) {
                if(message.getChannel().getIsPrivate()) {
                    channelActivity.setMessagesInPrivateChannels(channelActivity.getMessagesInPrivateChannels() + 1);
                } else {
                    channelActivity.setMessagesInPublicChannels(channelActivity.getMessagesInPublicChannels() + 1);
                }
            }
            activityMap.put(loggedUserDate, channelActivity);
        }

        return activityMap;
    }

    private Map<LocalDate, MessageActivity> getMessageActivityMap(Long workspaceId, boolean lastMonth) {
        List<LoggedUser> loggedUsers = null;
        if (lastMonth) {
            loggedUsers = this.getAllLoggedUsersForWorkspaceForLastMonth(workspaceId);
        } else {
            loggedUsers = this.getAllLoggedUsersForWorkspace(workspaceId);
        }

        Map<LocalDate, MessageActivity> activityMap = new HashMap<>();
        for(LoggedUser loggedUser : loggedUsers) {
            MessageActivity messageActivity;
            LocalDate loggedUserDate = loggedUser.getDateTime().toLocalDate();
            if (activityMap.containsKey(loggedUserDate)) {
                messageActivity = activityMap.get(loggedUserDate);
            } else {
                messageActivity = new MessageActivity();
                messageActivity.setDate(loggedUserDate);
            }

            for (Message message : loggedUser.getMessages()) {
                messageActivity.setMessages(messageActivity.getMessages() + 1);
            }
            activityMap.put(loggedUserDate, messageActivity);
        }

        return activityMap;
    }
}
