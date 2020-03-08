package jm.dao;

import jm.api.dao.MessageDAO;
import jm.dto.MessageDTO;
import jm.model.Message;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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


    @Override
    public Optional<MessageDTO> getMessageDtoById (Long id) {
        //TODO: Проверить как работает
        MessageDTO messageDTO = null;

        try {
            /*
            private String userName; **
            private String userAvatarUrl;**

            private String pluginName; **
            private String botNickName; **

            private String channelName;

             */
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
//                                                                              ", u.name AS \"userName\"," +
//                                                                              "u.avatar_url AS \"userAvatarUrl\", " +
//                                                                              "b.nick_name AS \"botNickName\", " +
//                                                                              "b.name AS \"pluginName\", " +
//                                                                              "c.name AS \"channelName\" " +
                                                                              "FROM messages m " +
                                                                              "WHERE m.id=:id ")
//                                 + , users u, bots b, channels c
//                                                                              "AND u.id = m.user_id AND b.id = m.bot_id AND c.id = m.channel_id")
                                              .setParameter("id", id)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(MessageDTO.class))
                                              .getSingleResult();

            messageDTO.setRecipientUserIds(getListRecipientUserIds(id));


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(messageDTO);
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

    @Override
    public List<MessageDTO> getAllMessageDto (Boolean isDeleted) {
        return null;
    }

    @Override
    public List<Message> getMessagesByChannelId (Long id, Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m where m.channelId =:channel_id and m.isDeleted = :is_deleted", Message.class)
                       .setParameter("channel_id", id)
                       .setParameter("is_deleted", isDeleted)
                       .getResultList();
    }

    @Override
    public List<MessageDTO> getMessageDtoListByChannelId (Long id, Boolean isDeleted) {
        return null;
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

    @Override
    public List<MessageDTO> getMessagesDtoByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return null;
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
    public List<Message> getStarredMessagesForUser(Long userId, Boolean isDeleted) {
        return entityManager.createQuery(
                "select sm from User u join u.starredMessages as sm where u.id = :user_id and sm.isDeleted = :is_deleted", Message.class)
                .setParameter("user_id", userId)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
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

    @Override
    public List<Message> getMessagesByIds(Set<Long> ids, Boolean isDeleted) {
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
    public List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted) {
        return entityManager.createQuery("select m from Message m join  m.recipientUsers u where u.id =:userId and m.isDeleted = :is_deleted", Message.class)
                .setParameter("userId", userId)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }
}