package jm.api.dao;

import jm.model.Bot;
import jm.model.SlashCommand;
import jm.model.Workspace;

import java.util.List;

public interface SlashCommandDao {

    List<SlashCommand> getAll();

    void persist(SlashCommand command);

    void deleteById(Long id);

    SlashCommand merge(SlashCommand command);

    SlashCommand getById(Long id);

    SlashCommand getByName(String commandName);

    List<SlashCommand> getByWorkspace(Workspace workspace);

    List<SlashCommand> getByBotId(Long id);





}
