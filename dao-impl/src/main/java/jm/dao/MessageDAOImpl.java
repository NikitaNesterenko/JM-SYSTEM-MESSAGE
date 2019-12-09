package jm.dao;

import jm.api.dao.MessageDAO;
import jm.model.message.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MessageDAOImpl extends AbstractDao<ChannelMessage> implements MessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(MessageDAOImpl.class);

    @Override
    public List<ChannelMessage> getMessageByContent(String word) {
        try {
            return entityManager.createQuery("select m from ChannelMessage m where m.content =:content", ChannelMessage.class)
                    .setParameter("content", word)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ChannelMessage> getMessagesByChannelId(Long id) {
        try {
            return entityManager.createQuery("select m from ChannelMessage m where m.channel.id =:channel_id", ChannelMessage.class)
                    .setParameter("channel_id", id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return entityManager
                    .createQuery("select m from ChannelMessage m where m.channel.id =:channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate order by m.dateCreate", ChannelMessage.class)
                    .setParameter("channel_id", id)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return entityManager
                    .createQuery("select m from ChannelMessage m where m.bot.id = :bot_id and m.channel.id = :channel_id and m.dateCreate >= :startDate and m.dateCreate <= :endDate order by m.dateCreate", ChannelMessage.class)
                    .setParameter("bot_id", botId)
                    .setParameter("channel_id", channelId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Message> getStarredMessagesForUser(Long id) {
        try {
            return entityManager.createQuery(
                    "select m from Message m join m.starredByWhom as sm where sm.id = :id",
                    Message.class
            )
                    .setParameter("id", id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
