package jm;

import jm.model.SlashCommand;

import java.util.List;

public interface SlashCommandService {

    List<SlashCommand> getAllSlashCommands();

    void createSlashCommand(SlashCommand slashCommand);

    void deleteSlashCommand(Long id);

    boolean updateSlashCommand(SlashCommand slashCommand);

    SlashCommand getSlashCommandById(Long id);

    SlashCommand getSlashCommandByName(String name);

    List<SlashCommand> getSlashCommandsByBotId(Long id);

    List<SlashCommand> getSlashCommandsByWorkspaceId(Long id);
}
