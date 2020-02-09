package jm;

import jm.api.dao.BotDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@Transactional
public class BotServiceImpl implements BotService {
    private static final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);

    private BotDAO botDAO;

    @Autowired
    public void setBotDAO(BotDAO botDAO) { this.botDAO = botDAO; }

    @Override
    public List<Bot> gelAllBots() { return botDAO.getAll(); }

    @Override
    public void createBot(Bot bot) { botDAO.persist(bot); }

    @Override
    public void deleteBot(Long id) { botDAO.deleteById(id); }

    @Override
    public void updateBot(Bot bot) { botDAO.merge(bot);
    }

    @Override
    public Bot getBotById(Long id) { return botDAO.getById(id); }

    @Override
    public List<Bot> GetBotsByWorkspaceId(Workspace workspace) {
        return botDAO.getBotsByWorkspaceId(workspace);
    }

    @Override
    public Set<Channel> getChannels(Bot bot) { return botDAO.getChannels(bot); }

    @Override
    public Bot getBotBySlashCommand(SlashCommand slashCommand) {
        return botDAO.getBotByCommand(slashCommand);
    }
}
