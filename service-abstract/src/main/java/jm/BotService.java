package jm;

import jm.dto.BotDTO;
import jm.model.Bot;
import jm.model.Channel;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotService {

    List<Bot> gelAllBots ();

    Bot createBot(Bot bot);

    void deleteBot (Long id);

    void updateBot (Bot bot);

    Bot getBotById(Long id);

    Bot getBotByName(String name);

    Optional<BotDTO> getBotDTOById (Long id);

    List<Bot> getBotsByWorkspaceId (Long workspaceId);

    List<BotDTO> getBotDtoListByWorkspaceId (Long id);

    Set<Channel> getChannels (Bot bot);

    Bot getBotBySlashCommandId (Long id);

    Bot getBotByBotDto(@NonNull BotDTO botDTO);

    BotDTO getBotDtoByBot(@NonNull Bot bot);

    Optional<Bot> findByToken(String token);
}
