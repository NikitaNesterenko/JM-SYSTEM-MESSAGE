package jm;

import jm.dto.SlashCommandDto;
import jm.model.SlashCommand;

import java.util.List;
import java.util.Optional;

public interface SlashCommandService {

    List<SlashCommand> getAllSlashCommands(); //+

    void createSlashCommand(SlashCommand slashCommand);

    void deleteSlashCommand(Long id);

    boolean updateSlashCommand(SlashCommand slashCommand);

    SlashCommand getSlashCommandById(Long id); //+

    SlashCommand getSlashCommandByName(String name); //+

    List<SlashCommand> getSlashCommandsByBotId(Long id); //+

    List<SlashCommand> getSlashCommandsByWorkspaceId(Long id); //+

    Optional<List<SlashCommandDto>> getAllSlashCommandDTO();

    Optional<SlashCommandDto> getSlashCommandDTOById(Long id);

    Optional<SlashCommandDto> getSlashCommandDTOByName(String name);

    Optional<List<SlashCommandDto>> getSlashCommandDTOByBotId(Long id);

    Optional<List<SlashCommandDto>> getSlashCommandDTOByWorkspaceId(Long id);

    SlashCommand getEntityFromDTO(SlashCommandDto slashCommandDTO);
}
