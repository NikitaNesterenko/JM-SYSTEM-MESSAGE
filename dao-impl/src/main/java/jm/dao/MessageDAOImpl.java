package jm.dao;

import jm.api.dao.MessageDAO;
import jm.model.message.ChannelMessage;
import jm.model.message.Message;
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
            return entityManager.createQuery("SELECT m FROM ChannelMessage m WHERE m.content =:content", ChannelMessage.class)
                    .setParameter("content", word)
                    .getResultList();
    }

    @Override
    public List<ChannelMessage> getMessagesByChannelId(Long id) {
            return entityManager.createQuery("SELECT m FROM ChannelMessage m WHERE m.channel.id =:channel_id", ChannelMessage.class)
                    .setParameter("channel_id", id)
                    .getResultList();
    }

    @Override
    public List<ChannelMessage> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
            return entityManager
                    .createQuery("SELECT m FROM ChannelMessage m WHERE m.channel.id =:channel_id AND m.dateCreate >= :startDate AND m.dateCreate <= :endDate ORDER BY m.dateCreate", ChannelMessage.class)
                    .setParameter("channel_id", id)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
    }

    @Override
    public List<ChannelMessage> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate) {
            return entityManager
                    .createQuery("SELECT m FROM ChannelMessage m WHERE m.bot.id = :bot_id AND m.channel.id = :channel_id AND m.dateCreate >= :startDate AND m.dateCreate <= :endDate ORDER BY m.dateCreate", ChannelMessage.class)
                    .setParameter("bot_id", botId)
                    .setParameter("channel_id", channelId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
    }

    @Override
    public List<ChannelMessage> getStarredMessagesForUser(Long id) {
            return entityManager.createQuery(
                    "SELECT m FROM Message m JOIN m.starredByWhom AS sm WHERE sm.id = :id",
                    ChannelMessage.class
            )
                    .setParameter("id", id)
                    .getResultList();
    }
}
