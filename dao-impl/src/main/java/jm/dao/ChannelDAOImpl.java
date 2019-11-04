package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

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
