package jm.api.dao;

import jm.dto.BotDTO;
import jm.model.Bot;
import jm.model.Channel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotDAO {

    List<Bot> getAll ();

    void persist (Bot bot);

    void deleteById (Long id);

    Bot save(Bot bot);

    Bot merge (Bot bot);

    Bot getById (Long id);

    Optional<BotDTO> getBotDTOById (Long id);

    List<Bot> getBotsByWorkspaceId (Long id);

    boolean haveBotWithName (String name);

    List<BotDTO> getBotDtoListByWorkspaceId (Long workspaceId);

    Set<Channel> getChannels (Bot bot);

    Optional<Bot> getBotByCommandId (Long id);

    Optional<Bot> findByToken(String token);
}
