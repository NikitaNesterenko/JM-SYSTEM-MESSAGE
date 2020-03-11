package jm.dao;

import jm.api.dao.SlashCommandDao;
import jm.dto.SlashCommandDTO;
import jm.model.SlashCommand;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<SlashCommand> getByWorkspaceId(Long id) {
        try {
            return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* " +
                    "FROM bots_slash_commands bc JOIN slash_commands sc " +
                    "JOIN workspaces_bots wb ON sc.id = bc.slash_command_id " +
                    "AND bc.bot_id = wb.bot_id WHERE wb.workspace_id=?", SlashCommand.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<SlashCommand> getByBotId(Long id) {
        try {
            return (List<SlashCommand>) entityManager.createNativeQuery("SELECT sc.* FROM bots_slash_commands bc JOIN slash_commands sc ON bc.slash_command_id = sc.id WHERE bc.bot_id=?", SlashCommand.class)
                    .setParameter(1, id)
                    .getResultList();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Optional<List<SlashCommandDTO>> getAllSlashCommandDTO() {
        List<SlashCommandDTO> slashCommandsDTO = null;
        try {
            slashCommandsDTO = (List<SlashCommandDTO>) entityManager.createNativeQuery("SELECT " +
                    "sc.id AS \"id\", " +
                    "sc.name AS \"name\", " +
                    "sc.url AS \"url\", " +
                    "sc.description AS \"description\", " +
                    "sc.hints AS \"hints\", " +
                    "sc.bot_id AS \"botId\", " +
                    "sc.type_id AS \"typeId\" " +
                    "FROM slash_commands sc")
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(SlashCommandDTO.class))
                    .getResultList();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.of(slashCommandsDTO);
    }

    @Override
    public Optional<SlashCommandDTO> getSlashCommandDTOById(Long id) {
        SlashCommandDTO slashCommandDTO = null;
        try {
            slashCommandDTO = (SlashCommandDTO) entityManager.createNativeQuery("SELECT " +
                    "sc.id AS \"id\", " +
                    "sc.name AS \"name\", " +
                    "sc.url AS \"url\", " +
                    "sc.description AS \"description\", " +
                    "sc.hints AS \"hints\", " +
                    "sc.bot_id AS \"botId\", " +
                    "sc.type_id AS \"typeId\" " +
                    "FROM slash_commands sc " +
                    "WHERE sc.id = :id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(SlashCommandDTO.class))
                    .getResultList().get(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(slashCommandDTO);
    }

    @Override
    public Optional<SlashCommandDTO> getSlashCommandDTOByName(String name) {
        SlashCommandDTO slashCommandDTO = null;
        try {
            slashCommandDTO = (SlashCommandDTO) entityManager.createNativeQuery("SELECT " +
                    "sc.id AS \"id\", " +
                    "sc.name AS \"name\", " +
                    "sc.url AS \"url\", " +
                    "sc.description AS \"description\", " +
                    "sc.hints AS \"hints\", " +
                    "sc.bot_id AS \"botId\", " +
                    "sc.type_id AS \"typeId\" " +
                    "FROM slash_commands sc " +
                    "WHERE sc.name = :name")
                    .setParameter("name", name)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(SlashCommandDTO.class))
                    .getResultList().get(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(slashCommandDTO);
    }

    @Override
    public Optional<List<SlashCommandDTO>> getSlashCommandDTOByBotId(Long id) {
        List<SlashCommandDTO> slashCommandsDTO = null;
        try {
            slashCommandsDTO = (List<SlashCommandDTO>) entityManager.createNativeQuery("SELECT " +
                    "sc.id AS \"id\", " +
                    "sc.name AS \"name\", " +
                    "sc.url AS \"url\", " +
                    "sc.description AS \"description\", " +
                    "sc.hints AS \"hints\", " +
                    "sc.bot_id AS \"botId\", " +
                    "sc.type_id AS \"typeId\" " +
                    "FROM slash_commands sc " +
                    "WHERE sc.bot_id = :id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(SlashCommandDTO.class))
                    .getResultList();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.of(slashCommandsDTO);
    }

    @Override
    public Optional<List<SlashCommandDTO>> getSlashCommandDTOByWorkspaceId(Long id) {
        List<SlashCommandDTO> slashCommandsDTO = null;
        try {
            slashCommandsDTO = (List<SlashCommandDTO>) entityManager.createNativeQuery("SELECT " +
                    "sc.id AS \"id\", " +
                    "sc.name AS \"name\", " +
                    "sc.url AS \"url\", " +
                    "sc.description AS \"description\", " +
                    "sc.hints AS \"hints\", " +
                    "sc.bot_id AS \"botId\", " +
                    "sc.type_id AS \"typeId\" " +
                    "FROM workspaces_bots wb " +
                    "JOIN slash_commands sc ON wb.bot_id = sc.bot_id " +
                    "WHERE wb.workspace_id = :id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(SlashCommandDTO.class))
                    .getResultList();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return Optional.of(slashCommandsDTO);
    }
}
