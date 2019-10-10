package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.User;
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
        return entityManager.createNativeQuery("SELECT * FROM Channel").getResultList();
    }

    @Override
    public void createChannel(Channel channel) {
        entityManager.persist(channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        Channel searchedChannel = entityManager.find(Channel.class, channel.getId());
        if (searchedChannel != null) {
            entityManager.remove(searchedChannel);
        }
    }

    @Override
    public void updateChannel(Channel channel) {
        entityManager.merge(channel);
        entityManager.flush();
    }

    @Override
    public Channel getChannelById(int id) {
        return entityManager.find(Channel.class, id);
    }

    @Override
    public Channel getChannelByName(String name) {
        try {
            return (Channel) entityManager.createNativeQuery("select * from Channel where name='" + name + "'").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Channel> getChannelsByOwner(User user) {
        try {
            return entityManager.createNativeQuery("select * from Role where owner_id='" + user.getId() + "'").getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
