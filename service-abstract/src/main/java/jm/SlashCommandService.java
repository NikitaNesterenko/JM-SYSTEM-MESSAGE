package jm;

import jm.dto.SlashCommandDTO;
import jm.model.SlashCommand;

import java.util.List;
import java.util.Optional;

public interface SlashCommandService {

    List<SlashCommand> getAllSlashCommands(); //+

    void createSlashCommand(SlashCommand slashCommand);

    void deleteSlashCommand(Long id);

    void updateSlashCommand(SlashCommand slashCommand);

    SlashCommand getSlashCommandById(Long id); //+

    SlashCommand getSlashCommandByName(String name); //+

    List<SlashCommand> getSlashCommandsByBotId(Long id); //+

    List<SlashCommand> getSlashCommandsByWorkspaceId(Long id); //+

    Optional<List<SlashCommandDTO>> getAllSlashCommandDTO();

    Optional<SlashCommandDTO> getSlashCommandDTOById(Long id);

    Optional<SlashCommandDTO> getSlashCommandDTOByName(String name);

    Optional<List<SlashCommandDTO>> getSlashCommandDTOByBotId(Long id);

    Optional<List<SlashCommandDTO>> getSlashCommandDTOByWorkspaceId(Long id);

    SlashCommand getEntityFromDTO(SlashCommandDTO slashCommandDTO);
}
