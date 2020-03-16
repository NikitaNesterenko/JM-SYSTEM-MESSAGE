package jm.dao;

import jm.api.dao.MessageDAO;
import jm.dto.MessageDTO;
import jm.model.Message;
import lombok.NonNull;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class MessageDAOImpl extends AbstractDao<Message> implements MessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(MessageDAOImpl.class);

    private List<Number> getListRecipientUserIds (Long messageId) {

        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT recipient_user_id FROM messages_recipient_users mru WHERE direct_message_id=:messageId")
                           .setParameter("messageId", messageId)
                           .getResultList();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Optional<Tuple> getUserNameAndAvatarUrlByUserId (Long userId) {
        Tuple tuple = null;
        try {
            tuple = (Tuple) entityManager.createNativeQuery("SELECT u.username AS \"userName\", u.avatar_url AS \"userAvatarUrl\" " +
                                                                    "FROM users u WHERE u.id=:userId", Tuple.class)
                                    .setParameter("userId", userId)
                                    .getSingleResult();
        } catch (NoResultException ignored) {
        }
        return Optional.ofNullable(tuple);
    }

    private Optional<Tuple> getPluginNameAndBotNickNameByBotId (Long botId) {
        Tuple tuple = null;
        try {
            tuple = (Tuple) entityManager.createNativeQuery("SELECT " +
                                                                    "b.name AS \"pluginName\", " +
                                                                    "b.nick_name AS \"botNickName\" " +
                                                                    "FROM bots b WHERE b.id=:botId", Tuple.class)
                                    .setParameter("botId", botId)
                                    .getSingleResult();
        } catch (NoResultException ignored) {

        }
        return Optional.ofNullable(tuple);
    }

    private String getChannelNameByChannelId(@NonNull Number channelId) {

        return (String) entityManager.createNativeQuery("Select c.name FROM channels c WHERE c.id= :channelId")
                .setParameter("channelId", channelId).getSingleResult();
    }

    @Override
    public Optional<MessageDTO> getMessageDtoById (Long id) {
        MessageDTO messageDTO = null;

        try {
            messageDTO = (MessageDTO) entityManager.createNativeQuery("SELECT " +
                                                                              "m.id AS \"id\", " +
                                                                              "m.channel_id AS \"channelId\", " +
                                                                              "m.content AS \"content\", " +
                                                                              "m.date_create AS \"dateCreate\", " +
                                                                              "m.filename AS \"filename\", " +
                                                                              "m.is_deleted AS \"isDeleted\", " +
                                                                              "m.voice_message \"voiceMessage\", " +
                                                                              "m.workspace_id AS \"workspaceId\", " +
                                                                              "m.bot_id AS \"botId\", " +
                                                                              "m.parent_message_id AS \"parentMessageId\", " +
                                                                              "m.shared_message_id AS \"sharedMessageId\", " +
                                                                              "m.user_id AS \"userId\" " +
                                                                              "FROM messages m " +
                                                                              "WHERE m.id=:id ")
                                              .setParameter("id", id)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(MessageDTO.class))
                                              .getSingleResult();

            messageDTO.setRecipientUserIds(getListRecipientUserIds(id));

            Long channelId = messageDTO.getChannelId();
            if (channelId != null) {
                messageDTO.setChannelName(getChannelNameByChannelId(channelId));
            }

            Optional<Tuple> userData = getUserNameAndAvatarUrlByUserId(messageDTO.getUserId());
            if (userData.isPresent()) {
                messageDTO.setUserName((String) userData.get()
                                                        .get("userName"));
                messageDTO.setUserAvatarUrl((String) userData.get()
                                                             .get("userAvatarUrl"));
            }

            Optional<Tuple> botData = getPluginNameAndBotNickNameByBotId(messageDTO.getBotId());
            if (botData.isPresent()) {
                messageDTO.setPluginName((String) botData.get()
                                                          .get("pluginName"));
                messageDTO.setBotNickName((String) botData.get()
                                                           .get("botNickName"));
            }


        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(messageDTO);
    }

    @Override
    public Optional<Long> getMessageIdByContent (String content) {
        Long id = null;

        try {
            id = (Long) entityManager.createNativeQuery("SELECT id FROM messages WHERE content= :content")
                                .setParameter("content", content)
                                .getSingleResult();
        } catch (NoResultException ignored) {

        }

        return Optional.ofNullable(id);
    }

    @Override
    public Optional<String> getMessageContentById (Long id) {
        String content = null;

        try {
            content = (String) entityManager.createNativeQuery("SELECT content FROM messages WHERE id= :id")
                                       .setParameter("id", id)
                                       .getSingleResult();
        } catch (NoResultException ignored) {

        }

        return Optional.ofNullable(content);
    }

    @Override
    public List<Message> getMessageByContent (String word, Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m where m.content =:content and m.isDeleted = :is_deleted", Message.class)
                       .setParameter("content", word)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    @Override
    public List<Message> getAll (Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m where m.isDeleted = :is_deleted", Message.class)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    private List<Number> getAllMessageId (Boolean isDeleted) {
        List list = new ArrayList<>();
        try {
            list = entityManager
                           .createNativeQuery("SELECT m.id FROM messages m WHERE m.is_deleted= :isDeleted")
                           .setParameter("isDeleted", isDeleted)
                           .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MessageDTO> getAllMessageDtoByIsDeleted (Boolean isDeleted) {
        return getAllMessageId(isDeleted).stream()
                       .map(Number::longValue)
                       .map(this::getMessageDtoById)
                       .map(Optional::get)
                       .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByChannelId (Long id, Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m where m.channelId =:channel_id and m.isDeleted = :is_deleted", Message.class)
                       .setParameter("channel_id", id)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    private List<Number> getAllMessageIdByChannelIdAndIsDeleted (Long channelId, Boolean isDeleted) {
        List list = new ArrayList<>();
        try {
            list = entityManager
                           .createNativeQuery("SELECT m.id FROM messages m where m.channel_id =: channelId AND m.is_deleted=:isDeleted")
                           .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MessageDTO> getMessageDtoListByChannelId (Long id, Boolean isDeleted) {
        return getAllMessageIdByChannelIdAndIsDeleted(id, isDeleted).stream()
                       .map(Number::longValue)
                       .map(this::getMessageDtoById)
                       .map(Optional::get)
                       .sorted(Comparator.comparing(MessageDTO::getDateCreate))
                       .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return entityManager
                       .createQuery("select m from Message m where m.channelId =:channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate and m.isDeleted = :is_deleted order by m.dateCreate", Message.class)
                       .setParameter("channel_id", id)
                       .setParameter("startDate", startDate)
                       .setParameter("endDate", endDate)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    private List<Number> getAllMessageIdByChannelIdForPeriod (Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        List list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT m.id FROM messages m " +
                                                           "WHERE m.channel_id = :channelId " +
                                                           "AND m.is_deleted= :isDeleted " +
                                                           "AND m.date_create >= :startDate " +
                                                           "AND m.date_create <= :endDate " +
                                                           "AND m.is_deleted= :isDeleted " +
                                                           "ORDER BY m.date_create")
                           .setParameter("channelId", channelId)
                           .setParameter("isDeleted", isDeleted)
                           .setParameter("startDate", startDate)
                           .setParameter("endDate", endDate)
                           .setParameter("isDeleted", isDeleted)
                           .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MessageDTO> getMessagesDtoByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return getAllMessageIdByChannelIdForPeriod(id, startDate, endDate, isDeleted).stream()
                       .map(Number::longValue)
                       .map(this::getMessageDtoById)
                       .map(Optional::get)
                       .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod (Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return entityManager
                       .createQuery("select m from Message m where m.bot.id = :bot_id and m.channelId = :channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate and m.isDeleted = :is_deleted order by m.dateCreate", Message.class)
                       .setParameter("bot_id", botId)
                       .setParameter("channel_id", channelId)
                       .setParameter("startDate", startDate)
                       .setParameter("endDate", endDate)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    @Override
    public List<Message> getStarredMessagesForUser (Long userId, Boolean isDeleted) {
        return entityManager.createQuery(
                "select sm from User u join u.starredMessages as sm where u.id = :user_id and sm.isDeleted = :is_deleted", Message.class)
                       .setParameter("user_id", userId)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId (Long userId, Long workspaceId, Boolean isDeleted) {
        return entityManager.createQuery(
                "select sm from User u " +
                        "join u.starredMessages as sm " +
                        "where u.id = :user_id " +
                        "and sm.workspaceId = :workspace_id " +
                        "and sm.isDeleted = :is_deleted",
                Message.class)
                       .setParameter("user_id", userId)
                       .setParameter("workspace_id", workspaceId)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    private List<Number> getAllStarredMessagesIdByUserId (Long userId) {
        List list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT usm.starred_messages_id FROM users_starred_messages usm WHERE usm.user_id = :userId")
                    .setParameter("userId", userId).getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MessageDTO> getStarredMessagesDTOForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
        List<MessageDTO> messageDTOS = new ArrayList<>();
        List<Number> messagesIds = getAllStarredMessagesIdByUserId(userId);
        if (!messagesIds.isEmpty()) {
            messageDTOS = messagesIds
                    .stream()
                    .map(Number::longValue)
                    .map(this::getMessageDtoById)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return messageDTOS;
    }

    @Override
    public List<Message> getMessagesByIds (Set<Long> ids, Boolean isDeleted) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                       .createQuery("select m from Message m where m.id in :ids and m.isDeleted = :is_deleted", Message.class)
                       .setParameter("ids", ids)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    @Override
    public List<Message> getAllMessagesReceivedFromChannelsByUserId (Long userId, Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m join  m.recipientUsers u where u.id =:userId and m.isDeleted = :is_deleted", Message.class)
                       .setParameter("userId", userId)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }
}