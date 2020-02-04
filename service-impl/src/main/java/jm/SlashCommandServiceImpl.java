package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.SlashCommandDao;
import jm.model.Bot;
import jm.model.SlashCommand;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class SlashCommandServiceImpl implements SlashCommandService{
    private static final Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    private SlashCommandDao slashCommandDao;

    @Autowired
    public void setSlashCommandDAO(SlashCommandDao slashCommandDAO){ this.slashCommandDao = slashCommandDAO; }

    @Override
    public List<SlashCommand> getAllSlashCommands() {
        return slashCommandDao.getAll();
    }

    @Override
    public void createSlashCommand(SlashCommand slashCommand) {
        slashCommandDao.persist(slashCommand);
    }

    @Override
    public void deleteSlashCommand(Long id) {
        slashCommandDao.deleteById(id);
    }

    @Override
    public void updateSlashCommand(SlashCommand slashCommand) {
        slashCommandDao.merge(slashCommand);
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
    public List<SlashCommand> getSlashCommandsByWorkspace(Workspace workspace) {
        return slashCommandDao.getByWorkspace(workspace);
    }


}
