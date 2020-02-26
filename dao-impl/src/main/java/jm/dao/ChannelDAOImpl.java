package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @Override
    public Channel getChannelByName(String name) {
        try {
            return (Channel) entityManager.createQuery("SELECT c FROM Channel c WHERE c.name = :id", Channel.class)
                    .setParameter("id", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        try {
            return (List<Channel>) entityManager.createQuery("SELECT c FROM Channel c WHERE c.user = :id", Channel.class)
                    .setParameter("id", user.getId())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        String query = "SELECT ch.id, ch.name, ch.is_private " +
                "FROM channels ch " +
                "LEFT JOIN channels_users chu ON chu.channel_id = ch.id " +
                "LEFT JOIN workspaces ws ON ch.workspace_id = ws.id " +
                "LEFT JOIN users u ON chu.user_id = u.id " +
                "WHERE (ws.id = :workspace_id AND u.id = :user_id) " +
                "GROUP BY ch.id";

        return entityManager.createNativeQuery(query, "ChannelDTOMapping")
                .setParameter("workspace_id", workspaceId)
                .setParameter("user_id", userId)
                .getResultList();
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
        return (List<Channel>) entityManager.createNativeQuery("SELECT * FROM channels WHERE workspace_id = :id", Channel.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<Channel> getChannelsByUserId(Long userId) {
        List<BigInteger> channelsIdentityNumbers = (List<BigInteger>) entityManager.createNativeQuery("SELECT cu.channel_id FROM channels_users cu WHERE cu.user_id = :id")
                .setParameter("id", userId)
                .getResultList();
        List<BigInteger> channelsIdentityNumbersArrayList = new ArrayList<>(channelsIdentityNumbers);
        List<Channel> channels = new ArrayList<>();
        for (BigInteger channelId : channelsIdentityNumbersArrayList) {
            Long channelLongId = channelId.longValue();
            Channel channel = getById(channelLongId);
            System.out.println(channel);
            channels.add(channel);
        }
        return channels;
    }

    @Override
    public List<Channel> getChannelsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("SELECT o FROM Channel o WHERE o.id IN :ids", Channel.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        entityManager.createNativeQuery("DELETE FROM messages m WHERE m.channel_Id = :Id")
                .setParameter("Id", id)
                .executeUpdate();
    }

    @Override
    public Channel merge(Channel channel) {
        if (channel.getArchived()) {
            entityManager.createNativeQuery("UPDATE messages m SET m.is_deleted = TRUE WHERE m.channel_id = :id")
                    .setParameter("id", channel.getId())
                    .executeUpdate();
        }
        return entityManager.merge(channel);
    }

    @Override
    public List<Channel> getArchivedChannels() {
        return entityManager.createQuery("SELECT c FROM Channel  c LEFT JOIN FETCH c.user WHERE c.archived = TRUE ", Channel.class).getResultList();
    }

    @Override
    public List<Channel> getPrivateChannels() {
        try {
            return entityManager.createQuery("SELECT c FROM Channel c WHERE c.isPrivate = TRUE ", Channel.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}