package jm.dao;

import jm.api.dao.ThreadChannelMessageDAO;
import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ThreadChannelMessageDAOImpl extends AbstractDao<ThreadChannelMessage> implements ThreadChannelMessageDAO {

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannel(ThreadChannel threadChannel) {
        try {
            return entityManager.createQuery("select m from ThreadChannelMessage m where m.threadChannel =: threadChannel", ThreadChannelMessage.class)
                    .setParameter("threadChannel", threadChannel)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannelId(Long id) {
        try {
            return entityManager.createQuery("select m from ThreadChannelMessage m where m.threadChannel.id =: id", ThreadChannelMessage.class)
                    .setParameter("id", id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
