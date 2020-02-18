package jm;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;

import java.util.List;
import java.util.Set;

public interface BotService {

    List<Bot> gelAllBots();

    void createBot(Bot bot);

    void deleteBot(Long id);

    void updateBot(Bot bot);

    Bot getBotById(Long id);

    List<Bot> getBotsByWorkspaceId(Long id);

    Set<Channel> getChannels(Bot bot);

    Bot getBotBySlashCommandId(Long id);
}
