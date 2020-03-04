package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @Override
    public Channel getChannelByName (String name) {
        try {
            return (Channel) entityManager.createNativeQuery("select * from channels where name=?", Channel.class)
                                     .setParameter(1, name)
                                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByNameWithoutFieldsUserIdsAndBotIds (String name) {
        ChannelDTO channelDTO = null;
        try {
            channelDTO = (ChannelDTO) entityManager
                                              .createNativeQuery("SELECT " +
                                                                         "c.id  AS \"id\", " +
                                                                         "c.name AS \"name\", " +
                                                                         "c.workspace_id AS \"workspaceId\", " +
                                                                         "c.owner_id AS \"ownerId\", " +
                                                                         "c.is_private AS \"isPrivate\", " +
                                                                         "c.created_date \"createdDate\", " +
                                                                         "c.topic AS \"topic\", " +
                                                                         "c.archived AS \"isArchived\", " +
                                                                         "c.is_app AS \"isApp\" " +
                                                                         "FROM channels c WHERE name=:name")
                                              .setParameter("name", name)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                                              .getResultList()
                                              .get(0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelDTO);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByIdWithoutFieldsUserIdsAndBotIds (Long id) {
        ChannelDTO channelDTO = null;
        try {
            channelDTO = (ChannelDTO) entityManager
                                              .createNativeQuery("SELECT " +
                                                                         "c.id  AS \"id\", " +
                                                                         "c.name AS \"name\", " +
                                                                         "c.workspace_id AS \"workspaceId\", " +
                                                                         "c.owner_id AS \"ownerId\", " +
                                                                         "c.is_private AS \"isPrivate\", " +
                                                                         "c.created_date \"createdDate\", " +
                                                                         "c.topic AS \"topic\", " +
                                                                         "c.archived AS \"isArchived\", " +
                                                                         "c.is_app AS \"isApp\" " +
                                                                         "FROM channels c WHERE id=:id")
                                              .setParameter("id", id)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                                              .getResultList()
                                              .get(0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelDTO);
    }

    @Override
    public Set<Long> getSetUserIdsByName (String name) {
        Set<Long> userIds = new HashSet<>();
        try {
            List list = entityManager.createNativeQuery("SELECT cu.user_id  FROM channels_users cu LEFT JOIN channels c on cu.channel_id = c.id WHERE name=:name")
                                .setParameter("name", name)
                                .getResultList();
            userIds = new HashSet<>(list);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    @Override
    public Set<Long> getSetBotIdsByName (String name) {
        Set<Long> botIds = new HashSet<>();
        try {
            List list = entityManager.createNativeQuery("SELECT cb.bot_id  FROM channels_bots cb LEFT JOIN channels c on cb.channel_id = c.id WHERE name=:name")
                                .setParameter("name", name)
                                .getResultList();
            botIds = new HashSet<>(list);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return botIds;
    }

    @Override
    public Optional<ChannelDTO> getIdByName (String name) {
        ChannelDTO channelDTO = null;
        try {
            channelDTO = (ChannelDTO) entityManager
                                              .createNativeQuery("SELECT " +
                                                                         "c.id  AS \"id\" " +
                                                                         "FROM channels c WHERE name=:name")
                                              .setParameter("name", name)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                                              .getResultList()
                                              .get(0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelDTO);
    }

    @Override
    public List<Channel> getChannelsByOwnerId (Long ownerId) {
        try {
            return (List<Channel>) entityManager.createNativeQuery("select * from channels where owner_id=?", Channel.class)
                                           .setParameter(1, ownerId)
                                           .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId) {
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
    public List<Channel> getChannelsByWorkspaceId (Long id) {
        return (List<Channel>) entityManager.createNativeQuery("select * from channels where workspace_id=?", Channel.class)
                                       .setParameter(1, id)
                                       .getResultList();
    }

    public List<Channel> getChannelsByUserId (Long userId) {
        List<BigInteger> channelsIdentityNumbers = (List<BigInteger>) entityManager.createNativeQuery("select channel_id from channels_users where user_id=?")
                                                                              .setParameter(1, userId)
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
    public List<Channel> getChannelsByIds (Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                       .createQuery("select o from Channel o where o.id in :ids", Channel.class)
                       .setParameter("ids", ids)
                       .getResultList();
    }

}
