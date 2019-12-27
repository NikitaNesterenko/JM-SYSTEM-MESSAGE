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
    public List<Message> getMessageByContent(String word) {
        return entityManager.createQuery("select m from Message m where m.content =:content", Message.class)
                .setParameter("content", word)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByChannelId(Long id) {
        return entityManager.createQuery("select m from Message m where m.channelId =:channel_id", Message.class)
                .setParameter("channel_id", id)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager
                .createQuery("select m from Message m where m.channelId =:channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate order by m.dateCreate", Message.class)
                .setParameter("channel_id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager
                .createQuery("select m from Message m where m.bot.id = :bot_id and m.channelId = :channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate order by m.dateCreate", Message.class)
                .setParameter("bot_id", botId)
                .setParameter("channel_id", channelId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
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
    public List<Message> getStarredMessagesForUser(Long userId) {
        return entityManager.createQuery(
                "select sm from User u join u.starredMessages as sm where u.id = :user_id",
                Message.class
        )
                .setParameter("user_id", userId)
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
    public List<Message> getMessagesByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select o from Message o where o.id in :ids", Message.class)
                .setParameter("ids", ids)
                .getResultList();
    }

}
