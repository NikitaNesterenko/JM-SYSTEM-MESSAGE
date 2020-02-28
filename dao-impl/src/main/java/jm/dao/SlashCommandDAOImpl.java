package jm.dao;

import jm.api.dao.SlashCommandDao;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SlashCommandDAOImpl extends AbstractDao<SlashCommand> implements SlashCommandDao {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandDAOImpl.class);


    @Override
    public Optional<SlashCommand> getByName(String commandName) {
        try {
            return Optional.ofNullable((SlashCommand) entityManager.createNativeQuery("select * from slash_commands where name=?", SlashCommand.class)
                    .setParameter(1, commandName)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<SlashCommand> getByWorkspaceId(Long id) {
        return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* FROM jm_message_system.bots_slash_commands bc JOIN slash_commands sc JOIN workspaces_bots wb ON sc.id = bc.slash_command_id AND bc.bot_id = wb.bot_id WHERE wb.workspace_id=?", SlashCommand.class)
                .setParameter(1, id)
                .getResultList();
    }

    @Override
    public List<SlashCommand> getByBotId(Long id) {
        try {
            return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* FROM bots_slash_commands bc JOIN slash_commands sc ON bc.slash_command_id = sc.id WHERE bc.bot_id=?", SlashCommand.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NullPointerException e) {
            return Collections.emptyList();
        }
    }
}
