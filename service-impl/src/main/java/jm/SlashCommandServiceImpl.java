package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.SlashCommandDao;
import jm.api.dao.TypeSlashCommandDAO;
import jm.dto.SlashCommandDto;
import jm.model.Bot;
import jm.model.SlashCommand;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SlashCommandServiceImpl implements SlashCommandService {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    private final SlashCommandDao slashCommandDao;
    private final BotDAO botDAO;
    private final TypeSlashCommandDAO typeSlashCommandDAO;

    @Autowired
    public SlashCommandServiceImpl(SlashCommandDao slashCommandDao, BotDAO botDAO, TypeSlashCommandDAO typeSlashCommandDAO) {
        this.slashCommandDao = slashCommandDao;
        this.botDAO = botDAO;
        this.typeSlashCommandDAO = typeSlashCommandDAO;
    }

    @Override
    public List<SlashCommand> getAllSlashCommands() {
        return slashCommandDao.getAll();
    }

    @Override
    public void createSlashCommand(SlashCommand slashCommand) {
        Bot bot = botDAO.getById(slashCommand.getBot().getId());

        slashCommand.setUrl("/app/bot/" + bot.getName().toLowerCase());
        slashCommandDao.persist(slashCommand);

        bot.getCommands().add(slashCommand);
        botDAO.merge(bot);
    }

    @Override
    public void deleteSlashCommand(Long id) {
        SlashCommand slashCommand = slashCommandDao.getById(id);
        Bot bot = botDAO.getBotByCommandId(id);
        bot.getCommands().remove(slashCommand);
        botDAO.merge(bot);
        slashCommandDao.deleteById(id);
    }

    @Override
    public Boolean updateSlashCommand(SlashCommand slashCommand) {
        SlashCommand existCommand = slashCommandDao.getById(slashCommand.getId());
        existCommand.setName(slashCommand.getName());
        existCommand.setDescription(slashCommand.getDescription());
        existCommand.setHints(slashCommand.getHints());
        existCommand.setType(slashCommand.getType());
        return slashCommandDao.updateSlashCommand(existCommand);
    }

    @Override
    public SlashCommand getSlashCommandById(Long id) {
        return slashCommandDao.getById(id);
    }

    @Override
    public SlashCommand getSlashCommandByName(String name) {
        return slashCommandDao.getByName(name);
    }

    @Override
    public List<SlashCommand> getSlashCommandsByBotId(Long id) {
        return slashCommandDao.getByBotId(id);
    }

    @Override
    public List<SlashCommand> getSlashCommandsByWorkspaceId(Long id) {
        return slashCommandDao.getByWorkspaceId(id);
    }

    @Override
    public Optional<List<SlashCommandDto>> getAllSlashCommandDTO() {
        return slashCommandDao.getAllSlashCommandDTO();
    }

    @Override
    public Optional<SlashCommandDto> getSlashCommandDTOById(Long id) {
        return slashCommandDao.getSlashCommandDTOById(id);
    }

    @Override
    public Optional<SlashCommandDto> getSlashCommandDTOByName(String name) {
        return slashCommandDao.getSlashCommandDTOByName(name);
    }

    @Override
    public Optional<List<SlashCommandDto>> getSlashCommandDTOByBotId(Long id) {
        return slashCommandDao.getSlashCommandDTOByBotId(id);
    }

    @Override
    public Optional<List<SlashCommandDto>> getSlashCommandDTOByWorkspaceId(Long id) {
        return slashCommandDao.getSlashCommandDTOByWorkspaceId(id);
    }

    @Override
    public SlashCommand getEntityFromDTO(SlashCommandDto slashCommandDTO) {
        if (slashCommandDTO == null) {
            return null;
        }

        SlashCommand sc = new SlashCommand(slashCommandDTO);
        sc.setType(typeSlashCommandDAO.getById(slashCommandDTO.getTypeId()));
        Bot bot = botDAO.getById(slashCommandDTO.getBotId());

        if (bot != null) {
            sc.setBot(bot);
        }

        return sc;
    }

    @Override
    public Optional<String> getSlashCommandNameById(@NonNull Long slashCommandId) {
        return slashCommandDao.getSlashCommandNameById(slashCommandId);
    }
}
