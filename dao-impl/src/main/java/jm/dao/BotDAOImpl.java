
package jm.dao;

import jm.api.dao.BotDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Set;

@Repository
@Transactional
public class BotDAOImpl extends AbstractDao<Bot> implements BotDAO {
    private static final Logger logger = LoggerFactory.getLogger(BotDAOImpl.class);

    @Override
    public Bot getBotByWorkspaceId(Workspace workspace) {
        try {
            return (Bot) entityManager.createNativeQuery("select * from bots where workspace_id=?", Bot.class)
                    .setParameter(1, workspace)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

//    @Override
//    public Bot getBotByWorkspaceId(Workspace workspace) {
//        return null;
//    }

    @Override
    public Set<Channel> getChannels(Bot bot) { return bot.getChannels(); }
}

