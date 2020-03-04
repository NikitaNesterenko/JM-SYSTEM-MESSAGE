package jm.dao;

import jm.api.dao.BotDAO;
import jm.model.Bot;
import jm.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public class BotDAOImpl extends AbstractDao<Bot> implements BotDAO {
    private static final Logger logger = LoggerFactory.getLogger(BotDAOImpl.class);

    @Override
    public Bot save(Bot bot) {
        return entityManager.merge(bot);
    }

    @Override
    public List<Bot> getBotsByWorkspaceId(Long id) {
        try {
            return (List<Bot>) entityManager.createNativeQuery("SELECT b.* FROM workspaces_bots wb JOIN bots b ON b.id = wb.bot_id WHERE wb.workspace_id=?", Bot.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Set<Channel> getChannels(Bot bot) {
        return bot.getChannels();
    }

    @Override
    public Bot getBotByCommandId(Long id) {
        try {
            return (Bot) entityManager.createNativeQuery("SELECT b.* FROM bots_slash_commands bc JOIN bots b ON b.id = bc.bot_id WHERE bc.slash_command_id=?", Bot.class)
                    .setParameter(1, id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Optional<Bot> findByToken(String token) {
        try {
            Bot bot = entityManager.createQuery("SELECT b FROM Bot b WHERE b.token = :token", Bot.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(bot);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}