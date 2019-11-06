package jm.api.dao;

import jm.model.Bot;
import jm.model.Workspace;

import java.util.List;

public interface BotDAO {

    List<Bot> getAll();

    void persist(Bot bot);

    void deleteById(Long id);

    Bot merge(Bot bot);

    Bot getById(Long id);

    Bot getBotByWorkspaceId(Workspace workspace);
}
