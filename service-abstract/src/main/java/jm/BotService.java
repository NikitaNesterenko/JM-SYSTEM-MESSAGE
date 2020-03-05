package jm;

import jm.dto.BotDTO;
import jm.model.Bot;
import jm.model.Channel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotService {

    List<Bot> gelAllBots ();

    void createBot (Bot bot);

    void deleteBot (Long id);

    void updateBot (Bot bot);

    Bot getBotById (Long id);

    Optional<BotDTO> getBotDTOById (Long id);

    List<Bot> getBotsByWorkspaceId (Long workspaceId);

    List<BotDTO> getBotDtoListByWorkspaceId (Long id);

    Set<Channel> getChannels (Bot bot);

    Bot getBotBySlashCommandId (Long id);
}
