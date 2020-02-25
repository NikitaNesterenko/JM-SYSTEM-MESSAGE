package jm.api.dao;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotDAO {

    List<Bot> getAll();

    void persist(Bot bot);

    void deleteById(Long id);

    Bot merge(Bot bot);

    Bot getById(Long id);

    List<Bot> getBotsByWorkspaceId(Long id);

    Set<Channel> getChannels(Bot bot);

    Optional<Bot> getBotByCommandId(Long id);
}
