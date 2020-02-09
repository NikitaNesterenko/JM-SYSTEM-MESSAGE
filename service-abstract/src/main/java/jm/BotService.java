package jm;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.Workspace;

import java.util.List;
import java.util.Set;

public interface BotService {

    List<Bot> getAllBots();

    void createBot(Bot bot);

    void deleteBot(Long id);

    void updateBot(Bot bot);

    Bot getBotById(Long id);

    Bot getBotByWorkspaceId(Workspace workspace);

    Set<Channel> getChannels(Bot bot);
}
