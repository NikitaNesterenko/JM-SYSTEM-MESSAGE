package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.ChannelDTO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @Override
    public Channel getChannelByName(String name) {
        try {
            return (Channel) entityManager.createNativeQuery("select * from channels where name=?", Channel.class)
                    .setParameter(1, name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        try {
            return (List<Channel>) entityManager.createNativeQuery("select * from channels where owner_id=?", Channel.class)
                    .setParameter(1, user.getId()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(String workspaceName, String login) {
        String query = "SELECT ch.id, ch.name " +
                "FROM channels_users chu " +
                "INNER JOIN channels ch ON chu.channel_id = ch.id " +
                "INNER JOIN workspaces ws ON ch.workspace_id = ws.id " +
                "INNER JOIN users u ON chu.user_id = u.id " +
                "WHERE (ws.name = :workspace AND ((u.login = :login AND ch.is_private = true) OR ch.is_private = false)) " +
                "GROUP BY ch.id";

        return entityManager.createNativeQuery(query, ChannelDTO.class)
                .setParameter("workspace", workspaceName)
                .setParameter("login", login)
                .getResultList();
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
        try {
            return (List<Channel>) entityManager.createNativeQuery("select * from channels where workspace_id=?", Channel.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

}
