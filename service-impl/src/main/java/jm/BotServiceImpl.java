package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.SlashCommandDao;
import jm.api.dao.WorkspaceDAO;
import jm.dto.BotDTO;
import jm.model.Bot;
import jm.model.Channel;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class BotServiceImpl implements BotService {
    private static final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);

    private final BotDAO botDAO;
    private final WorkspaceDAO workspaceDAO;
    private final ChannelDAO channelDAO;
    private final SlashCommandDao slashCommandDao;

    public BotServiceImpl(BotDAO botDAO, WorkspaceDAO workspaceDAO, ChannelDAO channelDAO, SlashCommandDao slashCommandDao) {
        this.botDAO = botDAO;
        this.workspaceDAO = workspaceDAO;
        this.channelDAO = channelDAO;
        this.slashCommandDao = slashCommandDao;
    }

    @Override
    public List<Bot> gelAllBots() {
        return botDAO.getAll();
    }

    @Override
    public Bot createBot(Bot bot) {
        bot.setToken(UUID.randomUUID().toString());
        bot.setDateCreate(LocalDateTime.now());
        bot.setToken(UUID.randomUUID().toString());
        bot.setIsDefault(false);

        return botDAO.save(bot);
    }

    @Override
    public void deleteBot(Long id) {
        botDAO.deleteById(id);
    }

    @Override
    public void updateBot(Bot bot) {
        Bot existingBot = botDAO.getById(bot.getId());
        existingBot.setName(bot.getName());
        existingBot.setNickName(bot.getNickName());
        existingBot.setToken(bot.getToken());
        botDAO.merge(existingBot);
    }

    @Override
    public Bot getBotById(Long id) {
        return botDAO.getById(id);
    }

    @Override
    public Optional<BotDTO> getBotDTOById(Long id) {
        return botDAO.getBotDTOById(id);
    }

    @Override
    public List<Bot> getBotsByWorkspaceId(Long id) {
        return botDAO.getBotsByWorkspaceId(id);
    }

    @Override
    public List<BotDTO> getBotDtoListByWorkspaceId(Long id) {
        return botDAO.getBotDtoListByWorkspaceId(id);
    }

    @Override
    public Set<Channel> getChannels(Bot bot) {
        return botDAO.getChannels(bot);
    }

    @Override
    public Bot getBotBySlashCommandId(Long id) {
        return botDAO.getBotByCommandId(id);
    }

    @Override
    public Bot getBotByBotDto(@NonNull BotDTO botDTO) {
        Bot bot = new Bot(botDTO);

        bot.setWorkspaces(
                Optional.ofNullable(botDTO.getWorkspacesId())
                        .map(workspacesId -> workspacesId.stream().map(workspaceDAO::getById).collect(Collectors.toSet()))
                        .orElse(new HashSet<>())
        );

        bot.setCommands(
                Optional.ofNullable(botDTO.getSlashCommandsIds())
                        .map(slashCommandsIds -> slashCommandsIds.stream().map(slashCommandDao::getById).collect(Collectors.toSet()))
                        .orElse(new HashSet<>())
        );

        bot.setChannels(
                Optional.ofNullable(botDTO.getChannelIds())
                        .map(channelIds -> channelIds.stream().map(channelDAO::getById).collect(Collectors.toSet()))
                        .orElse(new HashSet<>())
        );


        return bot;
    }

    @Override
    public BotDTO getBotDtoByBot(@NonNull Bot bot) {
        return new BotDTO(bot);
    }

    @Override
    public Optional<Bot> findByToken(String token) {
        return botDAO.findByToken(token);
    }

    @Override
    public Set<Channel> getChannelSetByBotId(@NonNull Long botId) {
        return null;
    }
}
