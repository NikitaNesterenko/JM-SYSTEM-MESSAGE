package jm.dao;

import jm.api.dao.ThreadChannelMessageDAO;
import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class ThreadChannelMessageDAOImpl extends AbstractDao<ThreadChannelMessage> implements ThreadChannelMessageDAO {

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannel(ThreadChannel threadChannel) {
        return entityManager.createQuery("select m from ThreadChannelMessage m where m.threadChannel =: threadChannel", ThreadChannelMessage.class)
                .setParameter("threadChannel", threadChannel)
                .getResultList();
    }

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannelId(Long id) {
            return entityManager.createQuery("select m from ThreadChannelMessage m where m.threadChannel.id =: id", ThreadChannelMessage.class)
                    .setParameter("id", id)
                    .getResultList();
    }
}
