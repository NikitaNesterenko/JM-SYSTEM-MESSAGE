package jm.api.dao;

import com.google.api.client.util.BackOff;
import jm.dto.SlashCommandDto;
import jm.model.SlashCommand;
import lombok.NonNull;

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

    Optional<List<SlashCommandDto>> getAllSlashCommandDTO();

    Optional<SlashCommandDto> getSlashCommandDTOById(Long id);

    Optional<SlashCommandDto> getSlashCommandDTOByName(String name);

    Optional<List<SlashCommandDto>> getSlashCommandDTOByBotId(Long id);

    Optional<List<SlashCommandDto>> getSlashCommandDTOByWorkspaceId(Long id);

    Optional<String> getSlashCommandNameById(@NonNull Long slashCommandId);

    Boolean updateSlashCommand(@NonNull SlashCommand slashCommand);
}
