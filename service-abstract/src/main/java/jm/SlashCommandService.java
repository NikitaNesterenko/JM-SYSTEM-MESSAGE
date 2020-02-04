package jm;

import jm.model.Bot;
import jm.model.SlashCommand;
import jm.model.Workspace;

import java.util.List;

public interface SlashCommandService {

    List<SlashCommand> getAllSlashCommands();

    void createSlashCommand(SlashCommand slashCommand);

    void deleteSlashCommand(Long id);

    void updateSlashCommand(SlashCommand slashCommand);

    SlashCommand getSlashCommandById(Long id);

    SlashCommand getSlashCommandByName(String name);

    List<SlashCommand> getSlashCommandsByBotId(Long id);

    List<SlashCommand> getSlashCommandsByWorkspace(Workspace workspace);
}
