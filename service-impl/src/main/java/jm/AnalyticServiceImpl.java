package jm;

import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.WorkspaceDAO;
import jm.model.Channel;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticServiceImpl implements AnalyticService {

    private ChannelDAO channelDAO;
    private MessageDAO messageDAO;
    private WorkspaceDAO workspaceDAO;

    @Autowired
    public void setChannelDAO(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @Autowired
    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    @Autowired
    public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public List<Channel> getAllChannelsForWorkspace(Long workspaceId, Boolean lastMonth) {
        return lastMonth
                ? channelDAO.getChannelByWorkspaceForLastMonth(workspaceId)
                : channelDAO.getChannelByWorkspace(workspaceId);
    }

    @Override
    public Integer getMessagesCountForWorkspace(Long workspaceId) {
        int counter = 0;
        List<Channel> channels = channelDAO.getChannelByWorkspace(workspaceId);
        for (Channel c: channels) {
            counter += messageDAO.getMessagesByChannelId(c.getId()).size();
        }

        return counter;
    }

    @Override
    public List<User> getUsersForWorkspaceById(Long workspaceId) {
        return workspaceDAO.getAllUsersForWorkspace(workspaceId);
    }

    @Override
    public List<User> getUsersForWorkspaceByIdForLastMonth(Long workspaceId) {
        List<User> users = workspaceDAO.getAllUsersForWorkspace(workspaceId);
        return users.stream().filter(
                user -> user.getCreatedDate()
                        .isAfter(
                                LocalDateTime.now().minus(
                                        30, ChronoUnit.DAYS)
                        )
        ).collect(Collectors.toList());
    }
}
