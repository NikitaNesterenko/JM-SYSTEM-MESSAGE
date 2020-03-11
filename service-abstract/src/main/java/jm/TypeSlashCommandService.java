package jm;

import jm.model.TypeSlashCommand;

import java.util.List;

public interface TypeSlashCommandService {

    List<TypeSlashCommand> getAllTypesSlashCommands();

    TypeSlashCommand getTypeSlashCommandById(Long id);

    TypeSlashCommand getTypeSlashCommandByName(String name);

    void createTypeSlashCommand(TypeSlashCommand type);

    void deleteTypeSlashCommand(Long id);

    void updateTypeSlashCommand(TypeSlashCommand type);
}
