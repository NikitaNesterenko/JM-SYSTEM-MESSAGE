package jm.api.dao;

import jm.dto.SlashCommandDTO;
import jm.model.SlashCommand;

import java.util.List;
import java.util.Optional;

public interface SlashCommandDao {

    List<SlashCommand> getAll();

    void persist(SlashCommand command);

    void deleteById(Long id);

    SlashCommand merge(SlashCommand command);

    SlashCommand getById(Long id);

    SlashCommand getByName(String commandName);

    List<SlashCommand> getByWorkspaceId(Long id);

    List<SlashCommand> getByBotId(Long id);

    Optional<List<SlashCommandDTO>> getAllSlashCommandDTO();

    Optional<SlashCommandDTO> getSlashCommandDTOById(Long id);

    Optional<SlashCommandDTO> getSlashCommandDTOByName(String name);

    Optional<List<SlashCommandDTO>> getSlashCommandDTOByBotId(Long id);

    Optional<List<SlashCommandDTO>> getSlashCommandDTOByWorkspaceId(Long id);


}
