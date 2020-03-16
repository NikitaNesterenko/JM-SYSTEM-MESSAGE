package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import lombok.NonNull;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @Override
    public Channel getChannelByName(String name) {
        try {
            return (Channel) entityManager.createNativeQuery("SELECT * FROM channels c WHERE c.name = :name", Channel.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByName(String name) {
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

            channelDTO.setUserIds(getListUserIdsByName(name));
            channelDTO.setBotIds(getListBotIdsByName(name));

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(channelDTO);
    }

    @Override
    public Optional<Long> getChannelIdByName(String chanelName) {

        Long channelId = null;

        try {
            channelId = (Long) entityManager.createNativeQuery("SELECT c.id FROM channels c WHERE c.name=:chanelName")
                    .setParameter("chanelName", chanelName)
                    .getSingleResult();
        } catch (NoResultException ignored) {

        }
        return Optional.ofNullable(channelId);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOById(Long id) {
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
                    .getSingleResult();
            channelDTO.setUserIds(getListUserIdsByName(channelDTO.getName()));
            channelDTO.setBotIds(getListBotIdsByName(channelDTO.getName()));

        } catch (NoResultException e) {
            logger.info("Error id: " + id);
            e.printStackTrace();
        }
        return Optional.ofNullable(channelDTO);
    }

    private List<Number> getListUserIdsByName(String name) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT cu.user_id  FROM channels_users cu LEFT JOIN channels c on cu.channel_id = c.id WHERE name=:name")
                    .setParameter("name", name)
                    .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Number> getListBotIdsByName(String name) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT cb.bot_id  FROM channels_bots cb LEFT JOIN channels c on cb.channel_id = c.id WHERE name=:name")
                    .setParameter("name", name)
                    .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<ChannelDTO> getIdByName(String name) {
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
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
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

    private List<Number> getAllChannelId() {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT c.id FROM channels c ")
                    .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChannelDTO> getAllChanelDTO() {
        return getAllChannelId()
                .stream()
                .map(Number::longValue)
                .map(this::getChannelDTOById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
        return (List<Channel>) entityManager.createNativeQuery("SELECT * FROM channels WHERE workspace_id = :id", Channel.class)
                .setParameter("id", id)
                .getResultList();
    }

    private List<Number> getAllChannelIdByWorkspaceId(Long id) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager
                    .createNativeQuery("SELECT wc.channel_id FROM workspaces_channels wc where wc.workspace_id=:id")
                    .setParameter("id", id)
                    .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByWorkspaceId(Long workspaceId) {
        return getAllChannelIdByWorkspaceId(workspaceId)
                .stream()
                .map(Number::longValue)
                .map(this::getChannelDTOById)
                .map(Optional::get)
                .collect(Collectors.toList());
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

    private List<Number> getAllChannelIdByUserId(Long id) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager
                    .createNativeQuery("SELECT cu.channel_id FROM channels_users cu WHERE cu.user_id=:id ")
                    .setParameter("id", id)
                    .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByUserId(Long userId) {
        return getAllChannelIdByUserId(userId)
                .stream()
                .map(Number::longValue)
                .map(this::getChannelDTOById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> getChannelsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select ch from Channel ch where ch.id in :ids", Channel.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public String getTopicChannelByChannelId(Long id) {
        String topic = (String) entityManager.createNativeQuery("select ch.topic from channels ch where ch.id=?")
                .setParameter(1, id)
                .getSingleResult();
        return topic == null ? "\"Add a topic\"" : topic;
    }

    @Override
    public Long getWorkspaceIdByChannelId(Long channelId) {
        BigInteger id = (BigInteger) entityManager.createNativeQuery("select ch.workspace_id from channels ch where ch.id=?")
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
    public Channel unzipChannel(Long id) {
        return (Channel) entityManager.createNativeQuery("UPDATE channels c SET c.archived = false WHERE c.id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Channel> getChannelListByBotId(@NonNull Long botId) {
        List<Channel> channelList = new ArrayList<>();
        try {
            channelList = entityManager.createNativeQuery("SELECT c.* FROM channels c, bots b, channels_bots cb " +
                                                                  "WHERE c.id = cb.channel_id AND b.id = cb.bot_id AND b.id= :botId", Channel.class)
                    .setParameter("botId", botId).getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return channelList;
    }
}
