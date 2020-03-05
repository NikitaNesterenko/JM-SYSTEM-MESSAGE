package jm.dao;

import jm.api.dao.BotDAO;
import jm.dto.BotDTO;
import jm.dto.ChannelDTO;
import jm.model.Bot;
import jm.model.Channel;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
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

    //TODO: переделать этот метод для практики
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
    public Bot getBotByCommandId (Long id) {
        try {
            return (Bot) entityManager.createNativeQuery("SELECT b.* FROM bots_slash_commands bc JOIN bots b ON b.id = bc.bot_id WHERE bc.slash_command_id=?", Bot.class)
                                 .setParameter(1, id)
                                 .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Optional<BotDTO> getBotDTOByIdWithoutFields_WorkspacesId_ChannelIds_SlashCommandsIds (Long id) {
        BotDTO botDTO = null;

        try {
            botDTO = (BotDTO) entityManager.createNativeQuery("SELECT b.id AS \"id\", b.name AS \"name\", b.nick_name AS \"nickName\", b.date_create  " +
                                                                      "FROM bots b WHERE id=:id")
                                      .setParameter("id", id)
                                      .unwrap(NativeQuery.class)
                                      .setResultTransformer(Transformers.aliasToBean(ChannelDTO.class))
                                      .getResultList()
                                      .get(0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(botDTO);
    }
}