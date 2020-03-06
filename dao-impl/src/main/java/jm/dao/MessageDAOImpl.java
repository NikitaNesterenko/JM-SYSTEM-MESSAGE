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

    private List<Long> getListRecipientUserIds () {

        List<Long> listIds = new ArrayList<>();


        return listIds;
    }


    @Override
    public Optional<MessageDTO> getMessageDtoById (Long id) {

        MessageDTO messageDTO = null;

        try {
            /*
            private Set<Long> recipientUserIds;

            private String userName;
            private String botNickName;
            private String channelName;
            private String userAvatarUrl;
            private String pluginName;
             */
            messageDTO = (MessageDTO) entityManager.createNativeQuery("SELECT " +
                                                                              "id AS \"id\", " +
                                                                              "channel_id AS \"channelId\", " +
                                                                              "content AS \"content\", " +
                                                                              "date_create AS \"dateCreate\", " +
                                                                              "filename AS \"filename\", " +
                                                                              "is_deleted AS \"isDeleted\", " +
                                                                              "voice_message \"voiceMessage\", " +
                                                                              "workspace_id AS \"workspaceId\", " +
                                                                              "bot_id AS \"botId\", " +
                                                                              "parent_message_id AS \"parentMessageId\", " +
                                                                              "shared_message_id AS \"sharedMessageId\", " +
                                                                              "user_id AS \"userId\" " +
                                                                              "FROM messages m WHERE id=:id")
                                              .setParameter("id", id)
                                              .unwrap(NativeQuery.class)
                                              .setResultTransformer(Transformers.aliasToBean(MessageDTO.class))
                                              .getSingleResult();


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(messageDTO);


        return null;
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