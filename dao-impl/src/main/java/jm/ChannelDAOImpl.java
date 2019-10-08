package jm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ChannelDAOImpl implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Channel> getAllChannels() {
        try {
            return (List<Channel>) entityManager.createNativeQuery("SELECT * from channels", Channel.class);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void createChannel(Channel channel) {
        entityManager.persist(channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        entityManager.remove(entityManager.contains(channel) ? channel : entityManager.merge(channel));
    }

    @Override
    public void updateChannel(Channel channel) {
        entityManager.merge(channel);
        entityManager.flush();
    }

    @Override
    public Channel getChannelById(int id) {
        try {
            return (Channel) entityManager.createNativeQuery("select * from channels where id=?", Channel.class)
                    .setParameter(1, id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Channel getChannelByName(String name) {
        try {
            return (Channel) entityManager.createNativeQuery("select * from channels where name=?", Channel.class)
                    .setParameter(1, name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        try {
            return (List<Channel>) entityManager.createNativeQuery("select * from channels where owner_id=?", Channel.class)
                    .setParameter(1, user.getId());
        } catch (NoResultException e) {
            return null;
        }
    }
}
