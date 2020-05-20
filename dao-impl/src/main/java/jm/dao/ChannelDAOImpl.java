package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.Workspace;
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
    public Optional<Channel> getChannelByName(String name) {
        BigInteger result = (BigInteger) entityManager.createNativeQuery("select exists (SELECT * FROM channels с WHERE name=:name)")
                .setParameter("name", name)
                .getSingleResult();
        boolean isExist = result.signum() > 0;

        if (isExist) {
            return Optional.of((Channel) entityManager.createNativeQuery("SELECT * FROM channels c WHERE c.name = :name", Channel.class)
                    .setParameter("name", name)
                    .getSingleResult());
        }
        return Optional.empty();
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByName(String name) {
        ChannelDTO channelDTO = null;

        BigInteger result = (BigInteger) entityManager.createNativeQuery("select exists (SELECT * FROM channels с WHERE name=:name)")
                .setParameter("name", name)
                .getSingleResult();
        boolean isExist = result.signum() > 0;

        if (isExist) {
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
                    .getSingleResult();

            channelDTO.setUserIds(getListUserIdsByName(name));
            channelDTO.setBotIds(getListBotIdsByName(name));
        }
        return Optional.ofNullable(channelDTO);
    }

    @Override
    public Optional<Long> getChannelIdByName(String name) {
        Long channelId = null;

        BigInteger result = (BigInteger) entityManager.createNativeQuery("select exists (SELECT * FROM channels с WHERE name=:name)")
                .setParameter("name", name)
                .getSingleResult();
        boolean isExist = result.signum() > 0;

        if (isExist) {
            channelId = (Long) entityManager.createNativeQuery("SELECT c.id FROM channels c WHERE c.name=:chanelName")
                    .setParameter("chanelName", name)
                    .getSingleResult();
        }

        return Optional.ofNullable(channelId);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOById(Long id) {
        ChannelDTO channelDTO = null;

        BigInteger result = (BigInteger) entityManager.createNativeQuery("select exists (SELECT * FROM channels с WHERE id=:id)")
                .setParameter("id", id)
                .getSingleResult();
        boolean isExist = result.signum() > 0;

        if (isExist) {
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
        }
        return Optional.ofNullable(channelDTO);
    }

    private List<Number> getListUserIdsByName(String name) {
        List<Number> list = new ArrayList<>();


        BigInteger result = (BigInteger) entityManager.createNativeQuery("select exists (SELECT * FROM channels с WHERE name=:name)")
                .setParameter("name", name)
                .getSingleResult();
        boolean isExist = result.signum() > 0;

        if (isExist) {
            list = entityManager.createNativeQuery("SELECT cu.user_id  FROM channels_users cu LEFT JOIN channels c on cu.channel_id = c.id WHERE name=:name")
                    .setParameter("name", name)
                    .getResultList();
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
                    .getSingleResult();

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
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        return (List<ChannelDTO>) entityManager.
                createNativeQuery("SELECT " +
                        "ch.id  AS \"id\", " +
                        "ch.name AS \"name\", " +
                        "ch.workspace_id AS \"workspaceId\", " +
                        "ch.owner_id AS \"ownerId\", " +
                        "ch.is_private AS \"isPrivate\", " +
                        "ch.created_date \"createdDate\", " +
                        "ch.topic AS \"topic\", " +
                        "ch.archived AS \"isArchived\", " +
                        "ch.is_app AS \"isApp\" " +
                        "FROM channels ch " +
                        "LEFT JOIN channels_users chu ON chu.channel_id = ch.id " +
                        "LEFT JOIN workspaces ws ON ch.workspace_id = ws.id " +
                        "LEFT JOIN users u ON chu.user_id = u.id " +
                        "WHERE (ws.id = :workspace_id AND u.id = :user_id) " +
                        "GROUP BY ch.id")
                .setParameter("workspace_id", workspaceId)
                .setParameter("user_id", userId)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
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
                    .createNativeQuery("SELECT id FROM channels where workspace_id = :id")
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
        return topic == null ? "\"Enter channel topic here.\"" : topic;
    }

    @Override
    public Workspace getWorkspaceByChannelId(Long channelId) {
        return Optional.ofNullable((Workspace) entityManager.createNativeQuery("SELECT * FROM workspaces WHERE id = (SELECT workspace_id from channels WHERE id = :channelId)", Workspace.class)
                .setParameter("channelId", channelId)
                .getSingleResult())
                .orElse(null);
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
}
