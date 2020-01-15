package jm.dao;

import jm.api.dao.ThreadChannelDAO;
import jm.model.ThreadChannel;
import jm.model.message.ChannelMessage;
import jm.model.message.ThreadChannelMessage;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ThreadChannelDAOImpl extends AbstractDao<ThreadChannel> implements ThreadChannelDAO {

    @Override
    public ThreadChannel getByChannelMessageId(Long id) {
        try {
            return entityManager.createQuery("select m from ThreadChannel m where m.message.id =:id", ThreadChannel.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
