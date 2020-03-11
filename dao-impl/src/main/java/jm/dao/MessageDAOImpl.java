package jm.dao;

import jm.api.dao.MessageDAO;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class MessageDAOImpl extends AbstractDao<Message> implements MessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(MessageDAOImpl.class);

    @Override
    public List<Message> getMessageByContent(String word, Boolean isDeleted) {
        return entityManager.createQuery("SELECT m FROM Message m WHERE m.content =:content AND m.isDeleted = :is_deleted", Message.class)
                .setParameter("content", word)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getAll(Boolean isDeleted) {
        return entityManager.createQuery("SELECT m FROM Message m WHERE m.isDeleted = :is_deleted", Message.class)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByChannelId(Long id, Boolean isDeleted) {
        return entityManager.createQuery("SELECT m FROM Message m WHERE m.channelId =:channel_id AND m.isDeleted = :is_deleted", Message.class)
                .setParameter("channel_id", id)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return entityManager
                .createQuery("SELECT m FROM Message m WHERE m.channelId =:channel_id AND m.dateCreate >= :startDate AND m.dateCreate <= :endDate AND m.isDeleted = :is_deleted ORDER BY m.dateCreate", Message.class)
                .setParameter("channel_id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return entityManager
                .createQuery("SELECT m FROM Message m WHERE m.bot.id = :bot_id AND m.channelId = :channel_id AND m.dateCreate >= :startDate AND m.dateCreate <= :endDate AND m.isDeleted = :is_deleted ORDER BY m.dateCreate", Message.class)
                .setParameter("bot_id", botId)
                .setParameter("channel_id", channelId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

//    @Override
//    public List<ChannelMessage> getStarredMessagesForUser(Long id) {
//            return entityManager.createQuery(
//                    "select m from Message m join m.starredByWhom as sm where sm.id = :id",
//                    ChannelMessage.class
//            )
//                    .setParameter("id", id)
//                    .getResultList();
//    }

    @Override
    public List<Message> getStarredMessagesForUser(Long userId, Boolean isDeleted) {
        return entityManager.createQuery(
                "SELECT sm FROM User u JOIN u.starredMessages AS sm WHERE u.id = :user_id AND sm.isDeleted = :is_deleted", Message.class)
                .setParameter("user_id", userId)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
        return entityManager.createQuery(
                "SELECT sm FROM User u " +
                        "JOIN u.starredMessages AS sm " +
                        "WHERE u.id = :user_id " +
                        "AND sm.workspaceId = :workspace_id " +
                        "AND sm.isDeleted = :is_deleted",
                Message.class)
                .setParameter("user_id", userId)
                .setParameter("workspace_id", workspaceId)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

//    @Override
//    public List<Message> getStarredMessagesForUser(Long id) {
//        try {
//            return (List<Message>) entityManager.createNativeQuery("SELECT * FROM messages WHERE id IN (SELECT starred_messages_id FROM users_starred_messages WHERE user_id = ?);", Message.class)
//                    .setParameter(1, id).getResultList();
//        } catch (NoResultException e) {
//            return null;
//        }
//    }

    @Override
    public List<Message> getMessagesByIds(Set<Long> ids, Boolean isDeleted) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("SELECT m FROM Message m WHERE m.id IN :ids AND m.isDeleted = :is_deleted", Message.class)
                .setParameter("ids", ids)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted) {
        return entityManager.createQuery("SELECT m FROM Message m JOIN  m.recipientUsers u WHERE u.id =:userId AND m.isDeleted = :is_deleted", Message.class)
                .setParameter("userId", userId)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }
}