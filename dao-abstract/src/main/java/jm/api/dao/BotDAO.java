package jm.api.dao;

import jm.model.WorkspaceApp.Bot;

import java.util.List;

public interface BotDAO {

    List<Bot> getAll();

    void persist(Bot bot);

    void deleteById(Long id);

    Bot merge(Bot bot);

    Bot getById(Long id);
}
