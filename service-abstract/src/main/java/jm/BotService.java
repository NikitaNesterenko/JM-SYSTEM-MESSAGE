package jm;

import jm.model.Bot;
import jm.model.Channel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotService {

    List<Bot> gelAllBots();

    Bot createBot(Bot bot);

    void deleteBot(Long id);

    void updateBot(Bot bot);

    Bot getBotById(Long id);

    List<Bot> getBotsByWorkspaceId(Long id);

    Set<Channel> getChannels(Bot bot);

    Bot getBotBySlashCommandId(Long id);

    Optional<Bot> findByToken(String token);
}
