package jm.api.dao;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;
import jm.model.Workspace;

import java.util.List;
import java.util.Set;

public interface BotDAO {

    List<Bot> getAll();

    void persist(Bot bot);

    void deleteById(Long id);

    Bot merge(Bot bot);

    Bot getById(Long id);

    List<Bot> getBotsByWorkspaceId(Workspace workspace);

    Set<Channel> getChannels(Bot bot);

    Bot getBotByCommand(SlashCommand slashCommand);
}
