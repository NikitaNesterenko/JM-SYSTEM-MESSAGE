package jm;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ChannelDAOImpl implements ChannelDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Channel> gelAllChannels() {
        return entityManager.createQuery("from Channel").getResultList();
    }

    @Override
    public void createChannel(Channel channel) {
        entityManager.persist(channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        entityManager.remove(entityManager.contains(channel)? channel : entityManager.merge(channel));
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
        return (Channel) entityManager.createQuery("from Channel where name  = :name")
                .setParameter("name", name)
                .getSingleResult();
    }
}
