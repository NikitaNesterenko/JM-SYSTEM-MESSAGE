package jm.dto;

import jm.api.dao.ChannelDAO;
import jm.api.dao.SlashCommandDao;
import jm.api.dao.WorkspaceDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;
import jm.model.Workspace;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BotDtoServiceImpl implements BotDtoService {

    private final ChannelDAO channelDAO;
    private final WorkspaceDAO workspaceDAO;
    private final SlashCommandDao slashCommandDao;

    public BotDtoServiceImpl(ChannelDAO channelDAO, WorkspaceDAO workspaceDAO, SlashCommandDao slashCommandDao) {
        this.channelDAO = channelDAO;
        this.workspaceDAO = workspaceDAO;
        this.slashCommandDao = slashCommandDao;
    }

    @Override
    public BotDTO toDto(Bot bot) {

        if (bot == null) {
            return null;
        }

        // creating new BotDTO with simple fields copied from Bot
        BotDTO botDTO = new BotDTO(bot);

        botDTO.setWorkspacesId(new HashSet<>());
        botDTO.setSlashCommandsIds(new HashSet<>());

        // setting up 'workspaceId'
        Set<Workspace> workspaces = bot.getWorkspaces();
        if ((workspaces != null) && !workspaces.isEmpty()) {
            workspaces.forEach(workspace -> botDTO.getWorkspacesId().add(workspace.getId()));
        }

        Set<SlashCommand> slashCommands = bot.getCommands();
        if ((slashCommands != null) && !slashCommands.isEmpty()) {
            slashCommands.forEach(slashCommand -> botDTO.getSlashCommandsIds().add(slashCommand.getId()));
        }

        // setting up 'channelIds'
        if (bot.getChannels() != null) {
            Set<Long> channelIds = bot.getChannels().stream().map(Channel::getId).collect(Collectors.toSet());
            botDTO.setChannelIds(channelIds);
        }

        return botDTO;
    }

    @Override
    @Transactional
    public Bot toEntity(BotDTO botDto) {

        if (botDto == null) {
            return null;
        }

        // creating new Bot with simple fields copied from BotDTO
        Bot bot = new Bot(botDto);

        // setting up 'workspace'
        Set<Long> ws = botDto.getWorkspacesId();
        if ((ws != null) && !ws.isEmpty()) {
            ws.forEach(id -> bot.getWorkspaces().add(workspaceDAO.getById(id)));
        }


        Set<Long> sc = botDto.getSlashCommandsIds();
        if ((sc != null) && !sc.isEmpty()) {
            sc.forEach(id -> bot.getCommands().add(slashCommandDao.getById(id)));
        }

        // setting up 'channels'
        Set<Long> channelIds = botDto.getChannelIds();
        List<Channel> channels = channelDAO.getChannelsByIds(channelIds);
        bot.setChannels(new HashSet<>(channels));

        return bot;
    }
}
