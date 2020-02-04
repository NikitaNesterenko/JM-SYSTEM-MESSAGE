package jm.dao;

import jm.api.dao.SlashCommandDao;
import jm.model.Bot;
import jm.model.SlashCommand;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class SlashCommandDAOImpl extends AbstractDao<SlashCommand> implements SlashCommandDao {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandDAOImpl.class);


    @Override
    public SlashCommand getByName(String commandName) {
       try {
           return (SlashCommand) entityManager.createNativeQuery("select * from slash_commands where name=?", SlashCommand.class)
                   .setParameter(1, commandName)
                   .getSingleResult();
       } catch (NoResultException e){
           return null;
       }
    }

    @Override
    public List<SlashCommand> getByWorkspace(Workspace workspace) {
        try {
            return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* FROM bots_commands bc JOIN slash_commands sc JOIN workspaces_bots wb ON sc.id = bc.commands_id AND bc.bot_id = wb.bot_id WHERE wb.workspace_id=?", SlashCommand.class)
                    .setParameter(1, workspace)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<SlashCommand> getByBotId(Long id) {
        try {
            return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* FROM bots_commands bc JOIN slash_commands sc ON bc.commands_id = sc.id WHERE bc.bot_id=?", SlashCommand.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
