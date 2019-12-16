package jm.api.dao;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BotDAO {

    List<Bot> getAll();

    void persist(Bot bot);

    void deleteById(Long id);

    Bot merge(Bot bot);

    Bot getById(Long id);

    Optional<Bot> getBotByWorkspaceId(Workspace workspace);

    Set<Channel> getChannels(Bot bot);
}
