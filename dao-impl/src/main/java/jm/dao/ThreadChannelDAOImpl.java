package jm.dao;

import jm.api.dao.ThreadChannelDAO;
import jm.model.ThreadChannel;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

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

    @Override
    public Optional<Number> getThreadIdByChannelMessageId(@NonNull Long channelMessageId) {
        Number threadDTO = null;

        try {
            threadDTO = (Number) entityManager.createNativeQuery("SELECT tc.id " +
                                                                         "FROM thread_channel tc " +
                                                                         "WHERE tc.message_id= :channelMessageId ")
                    .setParameter("channelMessageId", channelMessageId)
                    .getSingleResult();
        } catch (NoResultException ignored) {

        }
        return Optional.ofNullable(threadDTO);
    }
}
