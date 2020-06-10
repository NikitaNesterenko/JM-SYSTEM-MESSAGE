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
import java.util.*;
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
    public List<Bot> getBotsByWorkspaceId(Long id) {
            List<Bot> botList = (List<Bot>) entityManager.createNativeQuery("SELECT b.* FROM workspaces_bots wb JOIN bots b ON b.id = wb.bot_id WHERE wb.workspace_id=?", Bot.class)
                    .setParameter(1, id)
                    .getResultList();
        return botList.size()>0 ? botList : Collections.emptyList();
    }

    @Override
    public boolean haveBotWithName(String name) {
        return twoParametersMethodToSearchEntity("name", name);
    }

    private List<Number> getAllBotIdByWorkspaceId(Long workspaceId) {
            List<Number> list = entityManager
                    .createNativeQuery("SELECT wb.bot_id FROM workspaces_bots wb WHERE wb.workspace_id=:workspaceId")
                    .setParameter("workspaceId", workspaceId)
                    .getResultList();
        return list.size()>0 ? list : Collections.emptyList();
    }

    @Override
    public List<BotDTO> getBotDtoListByWorkspaceId(Long id) {
        return getAllBotIdByWorkspaceId(id).stream()
                .map(Number::longValue)
                .map(this::getBotDTOById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Channel> getChannels(Bot bot) {
        return bot.getChannels();
    }

    @Override
    public Optional<Bot> getBotByCommandId(Long id) {
            return Optional.ofNullable((Bot) entityManager.createNativeQuery("SELECT b.* FROM bots_slash_commands bc JOIN bots b ON b.id = bc.bot_id WHERE bc.slash_command_id=?", Bot.class)
                    .setParameter(1, id)
                    .getSingleResult());
    }


    private List<Number> getListWorkspacesIdByBotId(Long botId) {
        String hql = "SELECT wb.workspace_id FROM workspaces_bots wb WHERE wb.bot_id=:botId";
        List<Number> list = entityManager.createNativeQuery(hql)
                    .setParameter("botId", botId)
                    .getResultList();
        return list.size()>0 ? list : Collections.emptyList();
    }

    private List<Number> getListChannelIdsIdByBotId(Long botId) {
        String hql = "SELECT cb.channel_id FROM channels_bots cb WHERE cb.bot_id=:botId";
        List<Number> list = entityManager.createNativeQuery(hql)
                    .setParameter("botId", botId)
                    .getResultList();
        return list.size()>0 ? list : Collections.emptyList();
    }

    private List<Number> getListSlashCommandsIdsByBotId(Long botId) {
        String hql = "SELECT bsc.slash_command_id FROM bots_slash_commands bsc WHERE bsc.bot_id=:botId";
        List<Number> list = entityManager.createNativeQuery(hql)
                    .setParameter("botId", botId)
                    .getResultList();
        return list.size()>0 ? list : Collections.emptyList();
    }

    @Override
    public Optional<BotDTO> getBotDTOById(Long botId) {
        BotDTO botDTO = null;

        if (haveEntityWithThisId(botId)) {
            botDTO = (BotDTO) entityManager.createNativeQuery("SELECT b.id AS \"id\", b.name AS \"name\", b.nick_name AS \"nickName\", b.date_create AS \"dateCreate\" " +
                    "FROM bots b WHERE id=:id")
                    .setParameter("id", botId)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(BotDTO.class))
                    .getResultList()
                    .get(0);

            botDTO.setWorkspacesId(getListWorkspacesIdByBotId(botId));
            botDTO.setChannelIds(getListChannelIdsIdByBotId(botId));
            botDTO.setSlashCommandsIds(getListSlashCommandsIdsByBotId(botId));

        }
        return Optional.ofNullable(botDTO);
    }

    @Override
    public Optional<Bot> findByToken(String token) {
        if (twoParametersMethodToSearchEntity("token", token)) {
            Bot bot = entityManager.createQuery("SELECT b FROM Bot b WHERE b.token = :token", Bot.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(bot);
        }
        return Optional.empty();
    }
}