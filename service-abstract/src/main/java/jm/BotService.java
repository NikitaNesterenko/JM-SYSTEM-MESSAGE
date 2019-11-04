package jm;

import jm.model.WorkspaceApp.Bot;

import java.util.List;

public interface BotService {

    List<Bot> gelAllBots();

    void createBot(Bot bot);

    void deleteBot(Long id);

    void updateBot(Bot bot);

    Bot getBotById(Long id);

}
