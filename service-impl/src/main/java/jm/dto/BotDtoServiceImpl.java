package jm.dto;

import jm.api.dao.ChannelDAO;
import jm.api.dao.WorkspaceDAO;
import jm.model.Bot;
import jm.model.Channel;
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

    public BotDtoServiceImpl(ChannelDAO channelDAO, WorkspaceDAO workspaceDAO) {
        this.channelDAO = channelDAO;
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public BotDTO toDto(Bot bot) {

        if (bot == null) {
            return null;
        }

        BotDTO botDTO = new BotDTO(bot);

        Workspace workspace = bot.getWorkspace();
        if (workspace != null) {
            botDTO.setWorkspaceId(workspace.getId());
        }

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

        Bot bot = new Bot(botDto);
        if (botDto.getWorkspaceId() != null) {
            bot.setWorkspace(workspaceDAO.getById(botDto.getWorkspaceId()));
        }

        Set<Long> channelIds = botDto.getChannelIds();
        List<Channel> channels = channelDAO.getChannelsByIds(channelIds);
        bot.setChannels(new HashSet<>(channels));
        return bot;
    }
}
