package jm;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotService {

    List<Bot> gelAllBots();

    void createBot(Bot bot);

    void deleteBot(Long id);

    void updateBot(Bot bot);

    Bot getBotById(Long id);

    Optional<Bot> GetBotByWorkspaceId(Workspace workspace);

    Set<Channel> getChannels(Bot bot);
}
