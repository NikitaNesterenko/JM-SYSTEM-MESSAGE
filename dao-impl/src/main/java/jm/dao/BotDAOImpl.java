package jm.dao;

import jm.api.dao.BotDAO;
import jm.dto.BotDTO;
import jm.model.Bot;
import jm.model.Channel;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class BotDAOImpl extends AbstractDao<Bot> implements BotDAO {
    private static final Logger logger = LoggerFactory.getLogger(BotDAOImpl.class);

    @Override
    public Bot save(Bot bot) {
        return entityManager.merge(bot);
    }

    @Override
    public List<Bot> getBotsByWorkspaceId (Long id) {
        try {
            return (List<Bot>) entityManager.createNativeQuery("SELECT b.* FROM workspaces_bots wb JOIN bots b ON b.id = wb.bot_id WHERE wb.workspace_id=?", Bot.class)
                                       .setParameter(1, id)
                                       .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<Number> getAllBotIdByWorkspaceId (Long workspaceId) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager
                           .createNativeQuery("SELECT wb.bot_id FROM workspaces_bots wb WHERE wb.workspace_id=:workspaceId")
                           .setParameter("workspaceId", workspaceId)
                           .getResultList();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BotDTO> getBotDtoListByWorkspaceId (Long id) {
        return getAllBotIdByWorkspaceId(id).stream()
                       .map(Number::longValue)
                       .map(this::getBotDTOById)
                       .map(Optional::get)
                       .collect(Collectors.toList());
    }

    @Override
    public Set<Channel> getChannels (Bot bot) {
        return bot.getChannels();
    }

    @Override
    public Bot getBotByCommandId (Long id) {
        try {
            return (Bot) entityManager.createNativeQuery("SELECT b.* FROM bots_slash_commands bc JOIN bots b ON b.id = bc.bot_id WHERE bc.slash_command_id=?", Bot.class)
                                 .setParameter(1, id)
                                 .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    private List<Number> getListWorkspacesIdByBotId (Long botId) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT wb.workspace_id FROM workspaces_bots wb WHERE wb.bot_id=:botId")
                           .setParameter("botId", botId)
                           .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Number> getListChannelIdsIdByBotId (Long botId) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT cb.channel_id FROM channels_bots cb WHERE cb.bot_id=:botId")
                           .setParameter("botId", botId)
                           .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Number> getListSlashCommandsIdsByBotId (Long botId) {
        List<Number> list = new ArrayList<>();
        try {
            list = entityManager.createNativeQuery("SELECT bsc.slash_command_id FROM bots_slash_commands bsc WHERE bsc.bot_id=:botId")
                           .setParameter("botId", botId)
                           .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<BotDTO> getBotDTOById (Long id) {
        BotDTO botDTO = null;

        try {
            botDTO = (BotDTO) entityManager.createNativeQuery("SELECT b.id AS \"id\", b.name AS \"name\", b.nick_name AS \"nickName\", b.date_create AS \"dateCreate\" " +
                                                                      "FROM bots b WHERE id=:id")
                                      .setParameter("id", id)
                                      .unwrap(NativeQuery.class)
                                      .setResultTransformer(Transformers.aliasToBean(BotDTO.class))
                                      .getResultList()
                                      .get(0);

            botDTO.setWorkspacesId(getListWorkspacesIdByBotId(id));
            botDTO.setChannelIds(getListChannelIdsIdByBotId(id));
            botDTO.setSlashCommandsIds(getListSlashCommandsIdsByBotId(id));


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(botDTO);
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