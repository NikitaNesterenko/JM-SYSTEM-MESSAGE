package jm.dao;

import jm.api.dao.ThreadChannelDAO;
import jm.model.ThreadChannel;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class ThreadChannelDAOImpl extends AbstractDao<ThreadChannel> implements ThreadChannelDAO {

    @Override
    public Optional<ThreadChannel> getByChannelMessageId(Long id) {
        if (haveEntityWithThisId(id)) {
            return Optional.of(entityManager.createQuery("select m from ThreadChannel m where m.message.id =:id", ThreadChannel.class)
                    .setParameter("id", id)
                    .getSingleResult());
        }
        return Optional.empty();
    }
}
