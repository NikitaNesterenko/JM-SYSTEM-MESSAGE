package jm.api.dao;

import jm.model.TypeSlashCommand;

import java.util.List;
import java.util.Optional;

public interface TypeSlashCommandDAO {

    List<TypeSlashCommand> getAll();

    void persist(TypeSlashCommand type);

    void deleteById(Long id);

    TypeSlashCommand merge(TypeSlashCommand type);

    TypeSlashCommand getById(Long id);

    Optional<TypeSlashCommand> getByName(String name);
}
