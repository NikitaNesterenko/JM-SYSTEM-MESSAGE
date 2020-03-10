package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.User;
import org.hibernate.transform.Transformers;
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
//        String query = "SELECT c.id, c.created_date, c.name, c.workspace_id, c.topic, u.username FROM channels c JOIN users u ON u.id = c.owner_id WHERE c.name = :name";
        try {
            return (Channel) entityManager.createNativeQuery("SELECT * FROM channels c WHERE c.name = :name", Channel.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Channel> getChannelsByOwnerId(Long ownerId) {
        try {
            return (List<Channel>) entityManager.createNativeQuery("SELECT * FROM channels WHERE owner_id = ?", Channel.class)
                    .setParameter(1, ownerId)
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
                .createQuery("SELECT c FROM Channel c WHERE c.id IN :ids", Channel.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Long getWorkspaceIdByChannelId(Long channelId) {
        BigInteger id = (BigInteger) entityManager.createNativeQuery("SELECT c.workspace_id as  FROM channels c WHERE c.id = ?", Channel.class)
                .setParameter(1, channelId)
                .getSingleResult();
        return id.longValue();
    }

    @Override
    public List<ChannelDTO> getArchivedChannels() {
        return entityManager.createQuery("SELECT c.id as id, c.name as name, c.createdDate as createdDate, u.username as username FROM Channel c LEFT JOIN c.user u  WHERE c.archived = TRUE ORDER BY u.id")
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                .getResultList();
    }

    @Override
    public List<ChannelDTO> getPrivateChannels() {
        return entityManager.createQuery("SELECT c.id as id, c.name as name, c.createdDate as createdDate, u.username as username FROM Channel c LEFT JOIN c.user u  WHERE c.isPrivate = TRUE ORDER BY u.id")
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                .getResultList();
    }

    @Override
    public List<ChannelDTO> getAllChannel() {
        return entityManager.createQuery("SELECT c.id as id, c.name as name, c.createdDate as createdDate, u.username as username FROM  Channel c LEFT JOIN c.user u ORDER BY u.id")
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                .getResultList();
    }

    @Override
    public Channel unzipChannel(Long id) {
        return (Channel) entityManager.createNativeQuery("UPDATE channels c SET c.archived = false WHERE c.id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }
}
