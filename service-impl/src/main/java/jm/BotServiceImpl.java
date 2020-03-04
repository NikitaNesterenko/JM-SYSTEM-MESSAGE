package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.SlashCommandDao;
import jm.model.Bot;
import jm.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
public class BotServiceImpl implements BotService {
    private static final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);

    private BotDAO botDAO;
    private SlashCommandDao slashCommandDao;

    @Autowired
    public void setBotDAO(BotDAO botDAO) { this.botDAO = botDAO; }
    @Autowired
    public void setSlashCommandDao(SlashCommandDao slashCommandDao) {
        this.slashCommandDao = slashCommandDao;
    }

    @Override
    public List<Bot> gelAllBots() { return botDAO.getAll(); }

    @Override
    public Bot createBot(Bot bot) {
        bot.setToken(UUID.randomUUID().toString());
        bot.setDateCreate(LocalDateTime.now());
        bot.setToken(UUID.randomUUID().toString());

        return botDAO.save(bot);
    }

    @Override
    public void deleteBot(Long id) {botDAO.deleteById(id); }

    @Override
    public void updateBot(Bot bot) {
        botDAO.merge(bot);
    }

    @Override
    public Bot getBotById(Long id) { return botDAO.getById(id); }

    @Override
    public List<Bot> getBotsByWorkspaceId(Long id) {
        return botDAO.getBotsByWorkspaceId(id);
    }

    @Override
    public Set<Channel> getChannels(Bot bot) { return botDAO.getChannels(bot); }

    @Override
    public Bot getBotBySlashCommandId(Long id) {
        return botDAO.getBotByCommandId(id);
    }

    @Override
    public Optional<Bot> findByToken(String token) {
        return botDAO.findByToken(token);
    }

}
